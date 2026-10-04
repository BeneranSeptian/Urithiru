"""Run:  python3 -m unittest discover -s tests -v   (from the usecase-gen folder)"""
import difflib
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

HERE = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(HERE))
from ugen.errors import GenError  # noqa: E402
from ugen.kotlin import (add_imports, append_members, body_range, ensure_ctor_param, find_decl, has_fun,  # noqa: E402
                         mask, missing_imports)

GEN_MODEL = HERE.parent / "model-gen" / "gen-model"
GEN_UC = HERE / "gen-usecase"
BASE = "app/src/main/java/com/yourapp"
SVC = "example/sub/ExampleService.kt"
CLUSTER = "featureAUseCaseModule"

CONFIG = {
    "serialization": "gson",
    "paths": {
        "response": f"{BASE}/data/remote/response", "request": f"{BASE}/data/remote/request",
        "entity": f"{BASE}/data/entity", "model": f"{BASE}/domain/model",
        "service": f"{BASE}/data/remote/service", "dataSource": f"{BASE}/data/datasource",
        "dataSourceImpl": f"{BASE}/data/datasource/impl", "remoteModule": f"{BASE}/data/di",
        "repository": f"{BASE}/domain/repository", "repositoryImpl": f"{BASE}/data/repository",
        "dataModule": f"{BASE}/data/di", "useCase": f"{BASE}/domain/usecase",
        "useCaseModule": f"{BASE}/domain/di",
    },
    "imports": {
        "ToEntityMapper": "com.yourapp.core.mapper.ToEntityMapper", "ToModelMapper": "com.yourapp.core.mapper.ToModelMapper",
        "FromEntityMapper": "com.yourapp.core.mapper.FromEntityMapper", "FromModelMapper": "com.yourapp.core.mapper.FromModelMapper",
        "emptyString": "com.yourapp.core.util.emptyString", "BaseResponse": "com.yourapp.core.network.BaseResponse",
        "ResponseEntity": "com.yourapp.core.network.ResponseEntity",
        "DataState": "com.yourapp.core.state.DataState", "DataStateBoundResource": "com.yourapp.core.state.DataStateBoundResource",
    },
}
REMOTE_MODULE = """package com.yourapp.data.di

val remoteModule = aModule + bModule + cModule + module {
    factory { createService<AService>(get()) }
    factory { createService<BService>(get()) }
    // new service will go here
    factory<ARemoteDataSource> { ARemoteDataSourceImpl(get()) }
    factory<BRemoteDataSource> { BRemoteDataSourceImpl(get()) }
    //new datasource will go here
}
"""
DATA_MODULE = """package com.yourapp.data.di

val dataModule = aModule + bModule + cModule + module {
    factory<DRepository> { DRepositoryImpl(get(), get())}
    factory<ERepository> { ERepositoryImpl(get())}
}
"""
CLUSTER_FILE = """package com.yourapp.domain.di

internal val featureAUseCaseModule = module {
    factory { GetAUseCase(get()) }
    factory { GetBUseCase(get()) }
}

internal val featureBUseCaseModule = module {
    factory { GetCUseCase(get()) }
}

val usecaseModule = createList(
    featureAUseCaseModule,
    featureBUseCaseModule
)
"""
HILT_DI = {
    "container": r"\b(?:object|class)\s+\w*Module\b",
    "service": {"template": "@Provides\n@Singleton\nfun provide{Service}(retrofit: Retrofit): {Service} =\n"
                            "    retrofit.create({Service}::class.java)",
                "detect": [r"\bfun\s+provide{Service}\b"], "blank": True},
    "dataSource": {"template": "@Provides\n@Singleton\nfun provide{DataSource}({service}: {Service}): {DataSource} =\n"
                               "    {DataSourceImpl}({service})",
                   "detect": [r"\bfun\s+provide{DataSource}\b"], "blank": True},
    "repository": {"template": "@Provides\n@Singleton\nfun provide{Repository}(ds: {DataSource}): {Repository} =\n"
                               "    {RepositoryImpl}(ds)",
                   "detect": [r"\bfun\s+provide{Repository}\b"], "blank": True},
}
HILT_MODULE = "package com.yourapp.data.di\n\n@Module\nobject RemoteModule {\n    // keep } and { in comments safe\n    val s = \"}\"\n}\n"
HILT_DATA = "package com.yourapp.data.di\n\n@Module\nobject DataModule {\n}\n"


def removed_lines(old, new):
    return [l for l in difflib.unified_diff(old.splitlines(), new.splitlines(), lineterm="", n=0)
            if l.startswith("-") and not l.startswith("---")]


class KotlinHelpers(unittest.TestCase):
    def _append(self, src, name="A", block="fun b()", blank=False):
        m = mask(src)
        d = find_decl(m, name)
        o, c = body_range(m, d)
        return append_members(src, m, d, o, c, block, blank)

    def test_braces_in_strings_and_comments_are_ignored(self):
        t = 'interface A {\n    // }\n    val s = "}{"\n    fun x()\n}\n'
        m = mask(t)
        o, c = body_range(m, find_decl(m, "A"))
        self.assertEqual(t[c:], "}\n")

    def test_append_never_changes_existing_lines(self):
        for src in ("interface A {\n    fun a()\n}\n", "interface A {\n    fun a()\n\n}\n",
                    "interface A {\n    // only a comment\n}\n", "interface A {\n    fun a()\n    fun c()\n}\n",
                    "  interface A {\n      fun a()\n  }\n"):
            out = self._append(src)
            self.assertEqual(removed_lines(src, out), [], src)
            self.assertIn("fun b()", out)

    def test_append_blank_line_rules(self):
        self.assertIn("fun a()\n\n    fun b()", self._append("interface A {\n    fun a()\n}\n", blank=True))
        self.assertIn("fun a()\n    fun b()", self._append("interface A {\n    fun a()\n}\n", blank=False))
        already = self._append("interface A {\n    fun a()\n\n}\n", blank=True)
        self.assertNotIn("\n\n\n", already)

    def test_empty_one_line_body_is_the_allowed_exception(self):
        for src in ("interface A {}\n", "interface A { }\n"):
            self.assertEqual(self._append(src), "interface A {\n    fun b()\n}\n")

    def test_closing_brace_sharing_a_line_with_code_is_an_error(self):
        with self.assertRaises(GenError):
            self._append("interface A { fun a() }\n")

    def test_has_fun(self):
        self.assertTrue(has_fun("suspend fun getA(): X", "getA"))
        self.assertFalse(has_fun("suspend fun getAll(): X", "getA"))

    def test_ctor_param_cases(self):
        for src in ("class A : B {\n}\n", "class A() : B {\n}\n", "class A(\n    private val x: X\n) : B {\n}\n",
                    "class A(private val x: X) : B {\n}\n", "class A(\n    private val x: X,\n) : B {\n}\n"):
            out, prop = ensure_ctor_param(src, mask(src), find_decl(mask(src), "A"), "S", "s")
            self.assertEqual(prop, "s")
            self.assertIn("private val s: S", out)
        src = "class A(\n    private val svc: S\n) : B {\n}\n"
        self.assertEqual(ensure_ctor_param(src, mask(src), find_decl(mask(src), "A"), "S", "s"), (src, "svc"))

    def test_add_imports_for_new_files(self):
        t = "package p\n\nimport a.A\nimport c.C\n\nclass X\n"
        out = add_imports(t, ["b.B", "a.A", "z.*"])
        self.assertEqual(out.count("import a.A"), 1)
        self.assertLess(out.index("import b.B"), out.index("import c.C"))
        self.assertEqual(add_imports(out, ["z.Q"]), out)

    def test_missing_imports(self):
        t = "package p\n\nimport a.A\nimport w.*\n\nclass X\n"
        self.assertEqual(missing_imports(t, ["a.A", "w.Q", "p.Same", "q.R"]), ["q.R"])


class Base(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.cfg = self.root / "config.json"
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root)}))
        self.put("data/di/RemoteModule.kt", REMOTE_MODULE)
        self.put("data/di/DataModule.kt", DATA_MODULE)
        self.put("domain/di/UseCaseModule.kt", CLUSTER_FILE)
        (self.root / "resp.json").write_text('{"user_id": "a", "title": "t"}')
        (self.root / "req.json").write_text('{"id": "a", "name": "n"}')
        self.sh([GEN_MODEL, "Example", "--with-request", "--response", self.root / "resp.json",
                 "--request", self.root / "req.json", "--config", self.cfg, "--yes"])

    def tearDown(self):
        self.tmp.cleanup()

    def put(self, rel, text):
        p = self.root / BASE / rel
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(text)
        return p

    def read(self, rel):
        return (self.root / BASE / rel).read_text()

    def exists(self, rel):
        return (self.root / BASE / rel).exists()

    def snapshot(self):
        return {p: p.read_text() for p in self.root.rglob("*.kt")}

    def sh(self, args, ok=True):
        r = subprocess.run([sys.executable, *map(str, args)], capture_output=True, text=True)
        if ok:
            self.assertEqual(r.returncode, 0, r.stdout + r.stderr)
        return r

    def uc(self, usecase, service=SVC, endpoint="testing/v1", *extra, ok=True, cluster=CLUSTER):
        args = [GEN_UC, usecase, "--service", service, "--endpoint", endpoint, "--model", "Example",
                "--config", self.cfg, "--yes", *extra]
        if cluster:
            args += ["--cluster", cluster]
        return self.sh(args, ok)

    def assert_additions_only(self, before, exclude=()):
        for p, old in before.items():
            if p.name in exclude:
                continue
            self.assertEqual(removed_lines(old, p.read_text()), [], f"{p.name} lost lines")


class FullPipeline(Base):
    def test_get_then_post_whole_chain_and_rerun_is_noop(self):
        before = self.snapshot()
        self.uc("getExampleUseCase")
        self.uc("postExampleUseCase")

        svc = self.read("data/remote/service/example/sub/ExampleService.kt")
        self.assertIn('@GET("testing/v1")\n    suspend fun getExample(): BaseResponse<ResponseExample, ExampleEntity>', svc)
        self.assertIn("    @Body request: RequestExample\n    ): BaseResponse<ResponseExample, ExampleEntity>", svc)
        ds = self.read("data/datasource/example/sub/ExampleRemoteDataSource.kt")
        self.assertIn("suspend fun getExample(): ResponseEntity<ExampleEntity>", ds)
        self.assertIn("suspend fun postExample(entity: ExampleSpecEntity): ResponseEntity<ExampleEntity>", ds)
        impl = self.read("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt")
        self.assertIn("RequestExample.fromEntity(entity)", impl)

        self.assertEqual(self.read("domain/repository/example/sub/ExampleRepository.kt").count("interface ExampleRepository"), 1)
        repo = self.read("domain/repository/example/sub/ExampleRepository.kt")
        self.assertIn("suspend fun getExample(): DataState<Example>", repo)
        self.assertIn("suspend fun postExample(request: ExampleSpec): DataState<Example>", repo)
        rimpl = self.read("data/repository/example/sub/ExampleRepositoryImpl.kt")
        self.assertIn("class ExampleRepositoryImpl(\n    private val exampleRemoteDataSource: ExampleRemoteDataSource\n) : ExampleRepository {", rimpl)
        self.assertIn("override suspend fun getExample() = DataStateBoundResource.createNetworkCall {\n"
                      "        exampleRemoteDataSource.getExample()\n    }.getResult()", rimpl)
        self.assertIn("exampleRemoteDataSource.postExample(ExampleSpecEntity.fromModel(request))", rimpl)
        self.assertIn(".getResult()\n\n    override suspend fun postExample", rimpl)

        self.assertEqual(self.read("domain/usecase/GetExampleUseCase.kt"), (
            "package com.yourapp.domain.usecase\n\n"
            "import com.yourapp.core.state.DataState\nimport com.yourapp.domain.model.Example\n"
            "import com.yourapp.domain.repository.example.sub.ExampleRepository\n\n"
            "class GetExampleUseCase(private val exampleRepository: ExampleRepository) {\n"
            "    suspend fun invoke(): DataState<Example> {\n"
            "        return exampleRepository.getExample()\n    }\n}\n"))
        post_uc = self.read("domain/usecase/PostExampleUseCase.kt")
        self.assertIn("suspend fun invoke(request: ExampleSpec): DataState<Example> {\n"
                      "        return exampleRepository.postExample(request)", post_uc)
        self.assertIn("import com.yourapp.domain.model.ExampleSpec", post_uc)

        remote = self.read("data/di/RemoteModule.kt")
        self.assertIn("    factory { createService<BService>(get()) }\n    factory { createService<ExampleService>(get()) }\n"
                      "    // new service will go here", remote)
        self.assertIn("    factory<BRemoteDataSource> { BRemoteDataSourceImpl(get()) }\n"
                      "    factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }\n", remote)
        self.assertEqual(remote.count("createService<ExampleService>"), 1)
        self.assertIn("    factory<ERepository> { ERepositoryImpl(get())}\n"
                      "    factory<ExampleRepository> { ExampleRepositoryImpl(get()) }\n}", self.read("data/di/DataModule.kt"))
        cl = self.read("domain/di/UseCaseModule.kt")
        self.assertIn("    factory { GetBUseCase(get()) }\n    factory { GetExampleUseCase(get()) }\n"
                      "    factory { PostExampleUseCase(get()) }\n}", cl)
        self.assertIn("internal val featureBUseCaseModule = module {\n    factory { GetCUseCase(get()) }\n}", cl)

        self.assert_additions_only(before)
        again = self.snapshot()
        r = self.uc("postExampleUseCase")
        self.assertIn("Nothing to change", r.stdout)
        self.assertEqual(again, self.snapshot())

    def test_new_files_get_all_imports_existing_files_get_project_imports(self):
        r = self.uc("getExampleUseCase")
        new_svc = self.read("data/remote/service/example/sub/ExampleService.kt")
        self.assertIn("import com.yourapp.core.network.BaseResponse", new_svc)
        self.assertIn("import retrofit2.http.GET", new_svc)
        self.assertNotIn("toEntityWithData", self.read("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt").split("class ")[0])
        remote, data, cl = (self.read(f) for f in ("data/di/RemoteModule.kt", "data/di/DataModule.kt", "domain/di/UseCaseModule.kt"))
        for imp in ("data.remote.service.example.sub.ExampleService", "data.datasource.example.sub.ExampleRemoteDataSource",
                    "data.datasource.impl.example.sub.ExampleRemoteDataSourceImpl"):
            self.assertIn(f"import com.yourapp.{imp}", remote)
        self.assertIn("import com.yourapp.domain.repository.example.sub.ExampleRepository\n", data)
        self.assertIn("import com.yourapp.data.repository.example.sub.ExampleRepositoryImpl", data)
        self.assertIn("import com.yourapp.domain.usecase.GetExampleUseCase", cl)
        # second run: the service now exists -> POST adds its http imports + request class to the existing file
        self.uc("postExampleUseCase")
        svc = self.read("data/remote/service/example/sub/ExampleService.kt")
        for imp in ("retrofit2.http.POST", "retrofit2.http.Body", "com.yourapp.data.remote.request.RequestExample"):
            self.assertEqual(svc.count(f"import {imp}\n"), 1, imp)

    def test_existing_file_with_wildcard_imports(self):
        self.put("data/remote/service/example/sub/ExampleService.kt",
                 "package com.yourapp.data.remote.service.example.sub\n\nimport retrofit2.http.*\n\n"
                 "interface ExampleService {\n    @GET(\"old\")\n    suspend fun getOld(): Foo\n}\n")
        before = self.snapshot()
        r = self.uc("postExampleUseCase")
        svc = self.read("data/remote/service/example/sub/ExampleService.kt")
        self.assertIn("suspend fun postExample(", svc)
        self.assertNotIn("import retrofit2.http.POST", svc)             # covered by the wildcard
        self.assertNotIn("import retrofit2.http.Body", svc)
        for imp in ("data.remote.request.RequestExample", "data.remote.response.ResponseExample", "data.entity.ExampleEntity"):
            self.assertIn(f"import com.yourapp.{imp}\n", svc)          # project classes: added
        self.assertNotIn("import com.yourapp.core.network.BaseResponse", svc)   # base class: warning only
        self.assertIn("import com.yourapp.core.network.BaseResponse", r.stdout)
        self.assert_additions_only(before)

    def test_every_existing_file_kind_gets_its_missing_project_imports(self):
        self.put("data/datasource/example/sub/ExampleRemoteDataSource.kt", "package x\n\ninterface ExampleRemoteDataSource {\n}\n")
        self.put("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt",
                 "package x\n\nclass ExampleRemoteDataSourceImpl(\n    private val exampleService: ExampleService\n) : ExampleRemoteDataSource {\n}\n")
        self.put("domain/repository/example/sub/ExampleRepository.kt", "package x\n\ninterface ExampleRepository {\n}\n")
        self.put("data/repository/example/sub/ExampleRepositoryImpl.kt",
                 "package x\n\nclass ExampleRepositoryImpl(\n    private val exampleRemoteDataSource: ExampleRemoteDataSource\n) : ExampleRepository {\n}\n")
        before = self.snapshot()
        r = self.uc("postExampleUseCase")
        want = {
            "data/datasource/example/sub/ExampleRemoteDataSource.kt": ["data.entity.ExampleEntity", "data.entity.ExampleSpecEntity"],
            "data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt": ["data.remote.request.RequestExample", "data.entity.ExampleSpecEntity"],
            "domain/repository/example/sub/ExampleRepository.kt": ["domain.model.Example", "domain.model.ExampleSpec"],
            "data/repository/example/sub/ExampleRepositoryImpl.kt": ["data.entity.ExampleSpecEntity", "domain.model.ExampleSpec"],
        }
        for f, imps in want.items():
            txt = self.read(f)
            for imp in imps:
                self.assertEqual(txt.count(f"import com.yourapp.{imp}\n"), 1, f"{f} {imp}")
        for f, base in (("data/datasource/example/sub/ExampleRemoteDataSource.kt", "ResponseEntity"),
                        ("domain/repository/example/sub/ExampleRepository.kt", "DataState"),
                        ("data/repository/example/sub/ExampleRepositoryImpl.kt", "DataStateBoundResource")):
            self.assertNotIn(f"core.{'network' if base == 'ResponseEntity' else 'state'}.{base}", self.read(f))
            self.assertIn(base, r.stdout)
        self.assert_additions_only(before)

    def test_existing_impls_without_dependency_get_one_constructor_param_and_a_warning(self):
        self.put("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt",
                 "package x\n\nclass ExampleRemoteDataSourceImpl : ExampleRemoteDataSource {\n}\n")
        self.put("data/repository/example/sub/ExampleRepositoryImpl.kt",
                 "package y\n\nclass ExampleRepositoryImpl(\n    private val cache: Cache\n) : ExampleRepository {\n}\n")
        r = self.uc("getExampleUseCase")
        self.assertIn("class ExampleRemoteDataSourceImpl(\n    private val exampleService: ExampleService\n)",
                      self.read("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt"))
        self.assertIn("    private val cache: Cache,\n    private val exampleRemoteDataSource: ExampleRemoteDataSource\n)",
                      self.read("data/repository/example/sub/ExampleRepositoryImpl.kt"))
        self.assertEqual(r.stdout.count("added constructor parameter"), 2)
        self.assertIn("import com.yourapp.data.remote.service.example.sub.ExampleService\n",
                      self.read("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt"))
        self.assertIn("import com.yourapp.data.datasource.example.sub.ExampleRemoteDataSource\n",
                      self.read("data/repository/example/sub/ExampleRepositoryImpl.kt"))

    def test_empty_one_line_datasource_is_extended_but_code_on_closing_line_is_an_error(self):
        p = self.put("data/datasource/example/sub/ExampleRemoteDataSource.kt",
                     "package x\n\ninterface ExampleRemoteDataSource {}\n")
        self.uc("getExampleUseCase")
        self.assertIn("interface ExampleRemoteDataSource {\n    suspend fun getExample(): ResponseEntity<ExampleEntity>\n}",
                      p.read_text())
        p.write_text("package x\n\ninterface ExampleRemoteDataSource { fun a() }\n")
        before = self.snapshot()
        r = self.uc("postExampleUseCase", ok=False)
        self.assertIn("closing brace shares a line", r.stderr)
        self.assertEqual(before, self.snapshot())

    def test_existing_function_and_registrations_are_skipped(self):
        self.uc("getExampleUseCase")
        before = self.snapshot()
        r = self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "other/v2")
        self.assertIn("already has a function 'getExample'", r.stdout)
        self.assertIn("already registered", r.stdout)
        self.assertEqual(before, self.snapshot())

    def test_koin_modules_without_matching_lines_append_at_the_end(self):
        self.put("data/di/RemoteModule.kt", "package com.yourapp.data.di\n\nval remoteModule = module {\n}\n")
        self.put("data/di/DataModule.kt", "package com.yourapp.data.di\n\nval dataModule = module {\n}\n")
        self.uc("getExampleUseCase")
        self.assertIn("module {\n    factory { createService<ExampleService>(get()) }\n"
                      "    factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }\n}",
                      self.read("data/di/RemoteModule.kt"))
        self.assertIn("module {\n    factory<ExampleRepository> { ExampleRepositoryImpl(get()) }\n}",
                      self.read("data/di/DataModule.kt"))


class UseCaseAndCluster(Base):
    def test_unknown_cluster_is_an_error_and_writes_nothing(self):
        before = self.snapshot()
        r = self.uc("getExampleUseCase", cluster="nopeModule", ok=False)
        self.assertIn("cluster module 'nopeModule' not found", r.stderr)
        self.assertEqual(before, self.snapshot())

    def test_cluster_none_only_prints_the_line(self):
        before = self.read("domain/di/UseCaseModule.kt")
        r = self.uc("getExampleUseCase", cluster="none")
        self.assertEqual(before, self.read("domain/di/UseCaseModule.kt"))
        self.assertIn("To do by hand", r.stdout)
        self.assertIn("factory { GetExampleUseCase(get()) }", r.stdout)

    def test_named_cluster_gets_the_use_case_and_its_import(self):
        self.uc("getExampleUseCase")
        cl = self.read("domain/di/UseCaseModule.kt")
        self.assertIn("    factory { GetBUseCase(get()) }\n    factory { GetExampleUseCase(get()) }\n}", cl)
        self.assertIn("import com.yourapp.domain.usecase.GetExampleUseCase", cl)

    def test_use_case_that_already_exists_somewhere_is_skipped_not_duplicated(self):
        self.put("other/pkg/GetExampleUseCase.kt", "package other.pkg\n\nclass GetExampleUseCase\n")
        r = self.uc("getExampleUseCase")
        self.assertIn("SKIP    GetExampleUseCase already exists", r.stdout)
        self.assertFalse(self.exists("domain/usecase/GetExampleUseCase.kt"))
        self.assertTrue(self.exists("data/repository/example/sub/ExampleRepositoryImpl.kt"))

    def test_cluster_registration_is_not_duplicated(self):
        self.uc("getExampleUseCase")
        r = self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "x/v1")
        self.assertEqual(self.read("domain/di/UseCaseModule.kt").count("GetExampleUseCase(get())"), 1)

    def test_custom_request_entity_uses_the_model_from_its_from_model_mapper(self):
        self.put("data/entity/CustomEntity.kt", "package com.yourapp.data.entity\n\nclass CustomEntity {\n"
                 "    companion object : FromModelMapper<CustomModel, CustomEntity> {\n"
                 "        override fun fromModel(model: CustomModel) = CustomEntity()\n    }\n}\n")
        self.put("domain/model/CustomModel.kt", "package com.yourapp.domain.model\n\nclass CustomModel\n")
        self.uc("postExampleUseCase", SVC, "t/v1", "--request-entity", "CustomEntity")
        self.assertIn("suspend fun postExample(request: CustomModel): DataState<Example>",
                      self.read("domain/repository/example/sub/ExampleRepository.kt"))
        self.assertIn("exampleRemoteDataSource.postExample(CustomEntity.fromModel(request))",
                      self.read("data/repository/example/sub/ExampleRepositoryImpl.kt"))

    def test_request_entity_without_from_model_is_an_error(self):
        self.put("data/entity/PlainEntity.kt", "package com.yourapp.data.entity\n\nclass PlainEntity\n")
        r = self.uc("postExampleUseCase", SVC, "t/v1", "--request-entity", "PlainEntity",
                    "--request-model", "ExampleSpec", ok=False)
        self.assertIn("has no fromModel()", r.stderr)
        self.assertFalse(self.exists("data/remote/service/example/sub/ExampleService.kt"))


class InlineUseCaseModule(Base):
    """No cluster given: an inline `module { factory { X(get()) } },` element goes into createList(...)."""

    def test_inline_element_is_added_at_the_top_for_every_use_case(self):
        before = self.snapshot()
        self.uc("getExampleUseCase", cluster=None)
        self.uc("postExampleUseCase", cluster=None)
        cl = self.read("domain/di/UseCaseModule.kt")
        self.assertIn("val usecaseModule = createList(\n"
                      "    module { factory { PostExampleUseCase(get()) } },\n"
                      "    module { factory { GetExampleUseCase(get()) } },\n"
                      "    featureAUseCaseModule,\n    featureBUseCaseModule\n)", cl)
        self.assertIn("import com.yourapp.domain.usecase.GetExampleUseCase", cl)
        self.assert_additions_only(before)
        again = self.snapshot()
        self.assertIn("Nothing to change", self.uc("postExampleUseCase", cluster=None).stdout)
        self.assertEqual(again, self.snapshot())

    def test_already_registered_in_a_cluster_is_not_registered_again(self):
        p = self.root / BASE / "domain/di/UseCaseModule.kt"
        p.write_text(p.read_text().replace("factory { GetBUseCase(get()) }",
                                           "factory { GetBUseCase(get()) }\n    factory { GetExampleUseCase(get()) }"))
        self.uc("getExampleUseCase", cluster=None)
        self.assertEqual(p.read_text().count("GetExampleUseCase(get())"), 1)
        self.assertNotIn("module { factory { GetExampleUseCase", p.read_text())

    def test_empty_list_has_no_trailing_comma(self):
        self.put("domain/di/UseCaseModule.kt", "package com.yourapp.domain.di\n\nval usecaseModule = createList(\n)\n")
        self.uc("getExampleUseCase", cluster=None)
        self.assertIn("createList(\n    module { factory { GetExampleUseCase(get()) } }\n)", self.read("domain/di/UseCaseModule.kt"))

    def test_one_line_create_list_is_an_error_and_writes_nothing(self):
        self.put("domain/di/UseCaseModule.kt", "package x\n\nval usecaseModule = createList(a, b)\n")
        before = self.snapshot()
        r = self.uc("getExampleUseCase", cluster=None, ok=False)
        self.assertIn("elements on separate lines", r.stderr)
        self.assertEqual(before, self.snapshot())

    def test_missing_file_or_no_create_list_is_an_error(self):
        (self.root / BASE / "domain/di/UseCaseModule.kt").unlink()
        r = self.uc("getExampleUseCase", cluster=None, ok=False)
        self.assertIn("paths.useCaseModule", r.stderr)
        self.put("domain/di/UseCaseModule.kt", "package x\n\nval a = 1\n")
        r = self.uc("getExampleUseCase", cluster=None, ok=False)
        self.assertIn("no createList( call", r.stderr)

    def test_config_key_is_only_required_for_inline_mode(self):
        cfg = {**CONFIG, "projectRoot": str(self.root)}
        cfg["paths"] = {k: v for k, v in cfg["paths"].items() if k != "useCaseModule"}
        self.cfg.write_text(json.dumps(cfg))
        r = self.uc("getExampleUseCase", cluster=None, ok=False)
        self.assertIn("paths.useCaseModule", r.stderr)
        self.uc("getExampleUseCase", cluster=CLUSTER)          # named cluster works without it


class OtherBehaviour(Base):
    def test_errors_write_nothing(self):
        before = self.snapshot()
        r = self.sh([GEN_UC, "getMissingUseCase", "--service", "a/XService.kt", "--endpoint", "x", "--model", "Nope",
                     "--config", self.cfg, "--yes"], ok=False)
        self.assertIn("not found", r.stderr)
        self.assertEqual(before, self.snapshot())

    def test_duplicate_service_elsewhere_is_rejected(self):
        self.put("elsewhere/ExampleService.kt", "package x\ninterface ExampleService\n")
        r = self.uc("getExampleUseCase", ok=False)
        self.assertIn("already exists", r.stderr)

    def test_stage_selection_and_listing(self):
        self.uc("getExampleUseCase", "a/BService.kt", "t/v1", "--only", "service")
        self.assertTrue(self.exists("data/remote/service/a/BService.kt"))
        self.assertFalse(self.exists("data/datasource"))
        self.assertEqual(self.sh([GEN_UC, "--stages"]).stdout.count("\n"), 10)

    def test_method_inference_and_mismatch(self):
        self.uc("deleteExampleUseCase", "a/BService.kt", "t/v1", "--only", "service")
        self.assertIn('@DELETE("t/v1")', self.read("data/remote/service/a/BService.kt"))
        r = self.uc("getExampleUseCase", "a/CService.kt", '@POST("t")', ok=False)
        self.assertIn("mismatch", r.stderr)

    def test_default_and_custom_datasource_suffix(self):
        self.uc("getExampleUseCase", "a/OtherService.kt", "t/v1")
        self.assertTrue(self.exists("data/datasource/impl/a/OtherRemoteDataSourceImpl.kt"))
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root),
                                        "naming": {"dataSourceSuffix": "DataSource"}}))
        self.uc("getExampleUseCase", "b/AnotherService.kt", "t/v1")
        self.assertTrue(self.exists("data/datasource/impl/b/AnotherDataSourceImpl.kt"))
        self.assertIn("factory<AnotherDataSource> { AnotherDataSourceImpl(get()) }", self.read("data/di/RemoteModule.kt"))

    def test_hilt_override(self):
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root), "di": HILT_DI}))
        self.put("data/di/RemoteModule.kt", HILT_MODULE)
        self.put("data/di/DataModule.kt", HILT_DATA)
        self.uc("getExampleUseCase")
        mod = self.read("data/di/RemoteModule.kt")
        self.assertEqual(mod.count("fun provideExampleService"), 1)
        self.assertIn("@Singleton\n    fun provideExampleRemoteDataSource(exampleService: ExampleService)", mod)
        self.assertIn("fun provideExampleRepository(ds: ExampleRemoteDataSource): ExampleRepository =\n        ExampleRepositoryImpl(ds)",
                      self.read("data/di/DataModule.kt"))

    def test_only_the_config_a_stage_needs_is_required(self):
        cfg = {**CONFIG, "projectRoot": str(self.root)}
        cfg["paths"] = {k: v for k, v in cfg["paths"].items() if k not in ("repository", "repositoryImpl", "useCase", "useCaseModule", "dataModule")}
        self.cfg.write_text(json.dumps(cfg))
        self.uc("getExampleUseCase", "a/BService.kt", "t/v1", "--only", "service,datasource,datasource_impl,remote_di")
        r = self.uc("getExampleUseCase", "a/BService.kt", "t/v1", "--only", "repository", ok=False)
        self.assertIn("paths.repository", r.stderr)


if __name__ == "__main__":
    unittest.main()

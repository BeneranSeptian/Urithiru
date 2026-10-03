"""Run:  python3 -m unittest discover -s tests -v   (from the usecase-gen folder)"""
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

HERE = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(HERE))
from ugen.kotlin import add_imports, append_members, body_range, ensure_ctor_param, find_decl, has_fun, mask  # noqa: E402

GEN_MODEL = HERE.parent / "model-gen" / "gen-model"
GEN_UC = HERE / "gen-usecase"
BASE = "app/src/main/java/com/yourapp"
CONFIG = {
    "serialization": "gson",
    "paths": {
        "response": f"{BASE}/data/remote/response", "request": f"{BASE}/data/remote/request",
        "entity": f"{BASE}/data/entity", "model": f"{BASE}/domain/model",
        "service": f"{BASE}/data/remote/service", "dataSource": f"{BASE}/data/datasource",
        "dataSourceImpl": f"{BASE}/data/datasource/impl", "remoteModule": f"{BASE}/data/di",
    },
    "imports": {
        "ToEntityMapper": "com.yourapp.core.mapper.ToEntityMapper", "ToModelMapper": "com.yourapp.core.mapper.ToModelMapper",
        "FromEntityMapper": "com.yourapp.core.mapper.FromEntityMapper", "FromModelMapper": "com.yourapp.core.mapper.FromModelMapper",
        "emptyString": "com.yourapp.core.util.emptyString", "BaseResponse": "com.yourapp.core.network.BaseResponse",
        "ResponseEntity": "com.yourapp.core.network.ResponseEntity", "toEntityWithData": "com.yourapp.core.network.toEntityWithData",
    },
}
MODULE = """package com.yourapp.data.di

import org.koin.dsl.module

val remoteModule = aModule + bModule + cModule + module {
    factory { createService<AService>(get()) }
    factory { createService<BService>(get()) }
    // new service will go here
    factory<ARemoteDataSource> { ARemoteDataSourceImpl(get()) }
    factory<BRemoteDataSource> { BRemoteDataSourceImpl(get()) }
    //new datasource will go here
}
"""
HILT_MODULE = """package com.yourapp.data.di

import dagger.Module

@Module
object RemoteModule {
    // keep } and { in comments safe
    val s = "}"
}
"""
HILT_DI = {
    "container": r"\b(?:object|class)\s+\w*Module\b",
    "imports": ["dagger.Provides", "javax.inject.Singleton", "retrofit2.Retrofit"],
    "service": {"template": "@Provides\n@Singleton\nfun provide{Service}(retrofit: Retrofit): {Service} =\n"
                            "    retrofit.create({Service}::class.java)",
                "detect": [r"\bfun\s+provide{Service}\b"], "blank": True},
    "dataSource": {"template": "@Provides\n@Singleton\nfun provide{DataSource}({service}: {Service}): {DataSource} =\n"
                               "    {DataSourceImpl}({service})",
                   "detect": [r"\bfun\s+provide{DataSource}\b"], "blank": True},
}


class KotlinHelpers(unittest.TestCase):
    def test_braces_in_strings_and_comments_are_ignored(self):
        t = 'interface A {\n    // }\n    val s = "}{"\n    fun x()\n}\n'
        m = mask(t)
        d = find_decl(m, "A")
        o, c = body_range(m, d)
        self.assertEqual(t[c:], "}\n")

    def test_append_empty_and_nonempty(self):
        for src, expect_blank in (("interface A {}\n", False), ("interface A {\n    fun a()\n}\n", False)):
            m = mask(src)
            d = find_decl(m, "A")
            o, c = body_range(m, d)
            out = append_members(src, m, d, o, c, "fun b()", False)
            self.assertIn("    fun b()\n}", out)

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
        out, prop = ensure_ctor_param(src, mask(src), find_decl(mask(src), "A"), "S", "s")
        self.assertEqual((out, prop), (src, "svc"))

    def test_imports_sorted_and_deduped(self):
        t = "package p\n\nimport a.A\nimport c.C\n\nclass X\n"
        out = add_imports(t, ["b.B", "a.A", "z.*"])
        self.assertEqual(out.count("import a.A"), 1)
        self.assertLess(out.index("import b.B"), out.index("import c.C"))
        self.assertEqual(add_imports(out, ["z.Q"]), out)   # covered by wildcard


class EndToEnd(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.cfg = self.root / "config.json"
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root)}))
        (self.root / BASE / "data/di").mkdir(parents=True)
        (self.root / BASE / "data/di/RemoteModule.kt").write_text(MODULE)
        resp, req = self.root / "resp.json", self.root / "req.json"
        resp.write_text('{"user_id": "a", "title": "t"}')
        req.write_text('{"id": "a", "name": "n"}')
        self.sh([GEN_MODEL, "Example", "--with-request", "--response", resp, "--request", req,
                 "--config", self.cfg])

    def tearDown(self):
        self.tmp.cleanup()

    def sh(self, args, ok=True):
        r = subprocess.run([sys.executable, *map(str, args)], capture_output=True, text=True)
        if ok:
            self.assertEqual(r.returncode, 0, r.stdout + r.stderr)
        return r

    def uc(self, usecase, service, endpoint, *extra, ok=True):
        return self.sh([GEN_UC, usecase, "--service", service, "--endpoint", endpoint, "--model", "Example",
                        "--config", self.cfg, "--yes", *extra], ok)

    def read(self, rel):
        return (self.root / BASE / rel).read_text()

    def test_get_then_post_append_to_same_files_and_rerun_is_noop(self):
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        self.uc("postExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        svc = self.read("data/remote/service/example/sub/ExampleService.kt")
        self.assertIn('@GET("testing/v1")\n    suspend fun getExample(): BaseResponse<ResponseExample, ExampleEntity>', svc)
        self.assertIn("    @Body request: RequestExample\n    ): BaseResponse<ResponseExample, ExampleEntity>", svc)
        self.assertEqual(svc.count("interface ExampleService"), 1)
        ds = self.read("data/datasource/example/sub/ExampleRemoteDataSource.kt")
        self.assertIn("suspend fun postExample(entity: ExampleSpecEntity): ResponseEntity<ExampleEntity>", ds)
        impl = self.read("data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt")
        self.assertIn("RequestExample.fromEntity(entity)", impl)
        self.assertEqual(impl.count("class ExampleRemoteDataSourceImpl"), 1)
        mod = self.read("data/di/RemoteModule.kt")
        self.assertEqual(mod.count("createService<ExampleService>"), 1)
        self.assertEqual(mod.count("factory<ExampleRemoteDataSource>"), 1)
        before = {p: p.read_text() for p in self.root.rglob("*.kt")}
        r = self.uc("postExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        self.assertIn("Nothing to change", r.stdout)
        self.assertEqual(before, {p: p.read_text() for p in self.root.rglob("*.kt")})

    def test_koin_inserts_next_to_existing_entries_above_markers(self):
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        mod = self.read("data/di/RemoteModule.kt")
        expected = (
            "    factory { createService<BService>(get()) }\n"
            "    factory { createService<ExampleService>(get()) }\n"
            "    // new service will go here\n"
            "    factory<ARemoteDataSource> { ARemoteDataSourceImpl(get()) }\n"
            "    factory<BRemoteDataSource> { BRemoteDataSourceImpl(get()) }\n"
            "    factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }\n"
            "    //new datasource will go here\n}")
        self.assertIn(expected, mod)
        for imp in ("data.remote.service.example.sub.ExampleService", "data.datasource.example.sub.ExampleRemoteDataSource",
                    "data.datasource.impl.example.sub.ExampleRemoteDataSourceImpl"):
            self.assertIn(f"import com.yourapp.{imp}", mod)

    def test_koin_without_markers_uses_last_matching_line(self):
        (self.root / BASE / "data/di/RemoteModule.kt").write_text(MODULE.replace("    // new service will go here\n", "")
                                                                  .replace("    //new datasource will go here\n", ""))
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        mod = self.read("data/di/RemoteModule.kt")
        self.assertIn("createService<BService>(get()) }\n    factory { createService<ExampleService>(get()) }\n"
                      "    factory<ARemoteDataSource>", mod)
        self.assertIn("BRemoteDataSourceImpl(get()) }\n    factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }\n}", mod)

    def test_koin_empty_module(self):
        (self.root / BASE / "data/di/RemoteModule.kt").write_text("package com.yourapp.data.di\n\nval remoteModule = module {\n}\n")
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        mod = self.read("data/di/RemoteModule.kt")
        self.assertIn("module {\n    factory { createService<ExampleService>(get()) }\n    "
                      "factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }\n}", mod)

    def test_hilt_override_still_works(self):
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root), "di": HILT_DI}))
        (self.root / BASE / "data/di/RemoteModule.kt").write_text(HILT_MODULE)
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        mod = self.read("data/di/RemoteModule.kt")
        self.assertEqual(mod.count("fun provideExampleService"), 1)
        self.assertIn("@Singleton\n    fun provideExampleRemoteDataSource(exampleService: ExampleService)", mod)

    def test_default_and_custom_datasource_suffix(self):
        self.uc("getExampleUseCase", "a/OtherService.kt", "t/v1")
        self.assertTrue((self.root / BASE / "data/datasource/impl/a/OtherRemoteDataSourceImpl.kt").exists())
        self.cfg.write_text(json.dumps({**CONFIG, "projectRoot": str(self.root),
                                        "naming": {"dataSourceSuffix": "DataSource"}}))
        self.uc("getExampleUseCase", "b/AnotherService.kt", "t/v1")
        self.assertTrue((self.root / BASE / "data/datasource/impl/b/AnotherDataSourceImpl.kt").exists())
        self.assertIn("factory<AnotherDataSource> { AnotherDataSourceImpl(get()) }", self.read("data/di/RemoteModule.kt"))

    def test_existing_impl_without_service_param_gets_it(self):
        p = self.root / BASE / "data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt"
        p.parent.mkdir(parents=True)
        p.write_text("package com.yourapp.data.datasource.impl.example.sub\n\nclass ExampleRemoteDataSourceImpl : "
                     "ExampleRemoteDataSource {\n}\n")
        self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1")
        txt = p.read_text()
        self.assertIn("class ExampleRemoteDataSourceImpl(\n    private val exampleService: ExampleService\n)", txt)
        self.assertIn("override suspend fun getExample()", txt)

    def test_errors_write_nothing(self):
        r = self.sh([GEN_UC, "getMissingUseCase", "--service", "a/XService.kt", "--endpoint", "x", "--model", "Nope",
                     "--config", self.cfg, "--yes"], ok=False)
        self.assertNotEqual(r.returncode, 0)
        self.assertIn("not found", r.stderr)
        self.assertFalse((self.root / BASE / "data/remote/service").exists())

    def test_duplicate_service_elsewhere_is_rejected(self):
        other = self.root / BASE / "elsewhere/ExampleService.kt"
        other.parent.mkdir(parents=True)
        other.write_text("package x\ninterface ExampleService\n")
        r = self.uc("getExampleUseCase", "example/sub/ExampleService.kt", "testing/v1", ok=False)
        self.assertIn("already exists", r.stderr)

    def test_stage_selection(self):
        r = self.uc("getExampleUseCase", "a/BService.kt", "t/v1", "--only", "service")
        self.assertTrue((self.root / BASE / "data/remote/service/a/BService.kt").exists())
        self.assertFalse((self.root / BASE / "data/datasource").exists())

    def test_method_inference_and_mismatch(self):
        self.uc("deleteExampleUseCase", "a/BService.kt", "t/v1")
        self.assertIn('@DELETE("t/v1")', self.read("data/remote/service/a/BService.kt"))
        r = self.uc("getExampleUseCase", "a/CService.kt", '@POST("t")', ok=False)
        self.assertIn("mismatch", r.stderr)


if __name__ == "__main__":
    unittest.main()

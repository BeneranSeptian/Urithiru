from dataclasses import dataclass, field

from .names import BODY_METHODS, lower_first, upper_first


@dataclass
class Ctx:
    cfg: object
    fs: object
    index: object
    verbose: bool
    use_case: str           # "getExampleUseCase"
    func: str               # "getExample"
    method: str
    endpoint: str
    model: str              # "Example"
    svc_dir: str            # "example/sub"
    svc_name: str           # "ExampleService"
    req_entity: str         # "ExampleSpecEntity" (or a custom one)
    req_model: str = ""     # "ExampleSpec" (resolved by preflight)
    cluster: str = ""       # "" = inline in UseCaseModule.kt, "none" = only print, else a cluster module name
    interactive: bool = False
    selected: list = field(default_factory=list)
    classes: dict = field(default_factory=dict)    # existing project classes: name -> (path, package)
    pkgs: dict = field(default_factory=dict)       # generated artifacts: label -> package
    warnings: list = field(default_factory=list)
    notes: list = field(default_factory=list)

    # ---- names
    @property
    def has_body(self):
        return self.method in BODY_METHODS

    @property
    def _base(self):
        return self.svc_name[:-len("Service")]

    @property
    def ds_name(self):
        return self._base + self.cfg.naming["dataSourceSuffix"]

    @property
    def impl_name(self):
        return self.ds_name + "Impl"

    @property
    def repo_name(self):
        return self._base + "Repository"

    @property
    def repo_impl_name(self):
        return self.repo_name + "Impl"

    @property
    def ds_prop(self):
        return lower_first(self.ds_name)

    @property
    def repo_prop(self):
        return lower_first(self.repo_name)

    @property
    def usecase_class(self):
        return upper_first(self.use_case)

    # ---- paths
    @property
    def svc_path(self):
        return self.cfg.dir_of("service", self.svc_dir) / f"{self.svc_name}.kt"

    @property
    def ds_path(self):
        return self.cfg.dir_of("dataSource", self.svc_dir) / f"{self.ds_name}.kt"

    @property
    def impl_path(self):
        return self.cfg.dir_of("dataSourceImpl", self.svc_dir) / f"{self.impl_name}.kt"

    @property
    def repo_path(self):
        return self.cfg.dir_of("repository", self.svc_dir) / f"{self.repo_name}.kt"

    @property
    def repo_impl_path(self):
        return self.cfg.dir_of("repositoryImpl", self.svc_dir) / f"{self.repo_impl_name}.kt"

    @property
    def usecase_path(self):
        return self.cfg.dir_of("useCase") / f"{self.usecase_class}.kt"

    @property
    def module_path(self):
        return self.cfg.dir_of("remoteModule") / "RemoteModule.kt"

    @property
    def use_case_module_path(self):
        return self.cfg.dir_of("useCaseModule") / "UseCaseModule.kt"

    @property
    def data_module_path(self):
        return self.cfg.dir_of("dataModule") / "DataModule.kt"

    # ---- helpers
    def rel(self, p):
        return p.relative_to(self.cfg.root)

    def say(self, stage, msg):
        print(f"  [{stage}] {msg}")

    def debug(self, stage, msg):
        if self.verbose:
            print(f"  [{stage}] · {msg}")

    def warn(self, stage, msg):
        self.warnings.append(f"[{stage}] {msg}")

    def warn_imports(self, stage, path, missing):
        lines = "\n".join(f"      import {i}" for i in missing)
        self.warnings.append(f"[{stage}] {self.rel(path)} already exists. These imports come from config.json and "
                             f"are not added to existing files; add them if they are not covered:\n{lines}")

    def note(self, msg):
        self.notes.append(msg)

    def fq(self, name):
        """Fully qualified name of an existing project class resolved in preflight."""
        return f"{self.classes[name][1]}.{name}"

    def fill(self, s):
        for k, v in (("{DataSourceImpl}", self.impl_name), ("{RepositoryImpl}", self.repo_impl_name),
                     ("{DataSource}", self.ds_name), ("{Repository}", self.repo_name),
                     ("{Service}", self.svc_name), ("{service}", lower_first(self.svc_name))):
            s = s.replace(k, v)
        return s

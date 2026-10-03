from dataclasses import dataclass, field

from .names import BODY_METHODS


@dataclass
class Ctx:
    cfg: object
    fs: object
    index: object
    verbose: bool
    use_case: str
    func: str
    method: str
    endpoint: str
    model: str
    svc_dir: str            # "example/sub"
    svc_name: str           # "ExampleService"
    req_entity: str         # "ExampleSpecEntity" (or a custom one)
    classes: dict = field(default_factory=dict)   # name -> (path, package); filled by preflight
    svc_pkg: str = ""
    ds_pkg: str = ""
    impl_pkg: str = ""

    @property
    def has_body(self):
        return self.method in BODY_METHODS

    @property
    def ds_name(self):
        return self.svc_name[:-len("Service")] + self.cfg.naming["dataSourceSuffix"]

    @property
    def impl_name(self):
        return self.ds_name + "Impl"

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
    def module_path(self):
        return self.cfg.dir_of("remoteModule") / "RemoteModule.kt"

    def rel(self, p):
        return p.relative_to(self.cfg.root)

    def say(self, stage, msg):
        print(f"  [{stage}] {msg}")

    def debug(self, stage, msg):
        if self.verbose:
            print(f"  [{stage}] · {msg}")

    def fq(self, name):
        """Fully qualified name of a class resolved in preflight."""
        return f"{self.classes[name][1]}.{name}"

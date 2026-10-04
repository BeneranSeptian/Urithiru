"""Register the service and the datasource in RemoteModule.kt (below the last matching line)."""
from .di_common import register

NAME = "remote_di"


def run(ctx):
    di = ctx.cfg.di
    register(ctx, NAME, ctx.module_path, [
        {"label": ctx.svc_name, "spec": di["service"],
         "imports": [f"{ctx.pkgs['service']}.{ctx.svc_name}"]},
        {"label": ctx.ds_name, "spec": di["dataSource"],
         "imports": [f"{ctx.pkgs['datasource']}.{ctx.ds_name}", f"{ctx.pkgs['impl']}.{ctx.impl_name}"]},
    ])

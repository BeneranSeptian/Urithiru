"""Register the repository in DataModule.kt (below the last matching line)."""
from .di_common import register

NAME = "data_di"


def run(ctx):
    register(ctx, NAME, ctx.data_module_path, [
        {"label": ctx.repo_name, "spec": ctx.cfg.di["repository"],
         "imports": [f"{ctx.pkgs['repository']}.{ctx.repo_name}",
                     f"{ctx.pkgs['repository_impl']}.{ctx.repo_impl_name}"]},
    ])

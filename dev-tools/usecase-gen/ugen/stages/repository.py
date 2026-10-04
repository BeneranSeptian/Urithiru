"""Add the function to the repository interface (new file or existing)."""
from .common import upsert

NAME = "repository"


def run(ctx):
    m = ctx.model
    params = f"request: {ctx.req_model}" if ctx.has_body else ""
    member = f"suspend fun {ctx.func}({params}): DataState<{m}>"

    imports = [ctx.cfg.imports["DataState"], ctx.fq(m)]
    if ctx.has_body:
        imports.append(ctx.fq(ctx.req_model))

    upsert(ctx, NAME, ctx.repo_path, ctx.pkgs["repository"], ctx.repo_name, f"interface {ctx.repo_name}",
           member, ctx.func, imports, blank_default=None)

"""Add the function to the datasource interface (new file or existing)."""
from .common import upsert

NAME = "datasource"


def run(ctx):
    m = ctx.model
    ret = f"ResponseEntity<{m}Entity>"
    params = f"entity: {ctx.req_entity}" if ctx.has_body else ""
    member = f"suspend fun {ctx.func}({params}): {ret}"

    imports = [ctx.cfg.imports["ResponseEntity"], ctx.fq(f"{m}Entity")]
    if ctx.has_body:
        imports.append(ctx.fq(ctx.req_entity))

    upsert(ctx, NAME, ctx.ds_path, ctx.ds_pkg, ctx.ds_name, f"interface {ctx.ds_name}",
           member, ctx.func, imports, blank_default=False)

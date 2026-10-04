"""Add the Retrofit function to the service interface (new file or existing)."""
from .common import upsert

NAME = "service"


def run(ctx):
    m = ctx.model
    ret = f"BaseResponse<Response{m}, {m}Entity>"
    if ctx.has_body:
        fn = f"suspend fun {ctx.func}(\n    @Body request: Request{m}\n): {ret}"
    else:
        fn = f"suspend fun {ctx.func}(): {ret}"
    member = f'@{ctx.method}("{ctx.endpoint}")\n{fn}'

    imports = [ctx.cfg.imports["BaseResponse"], f"retrofit2.http.{ctx.method}",
               ctx.fq(f"Response{m}"), ctx.fq(f"{m}Entity")]
    if ctx.has_body:
        imports += ["retrofit2.http.Body", ctx.fq(f"Request{m}")]

    upsert(ctx, NAME, ctx.svc_path, ctx.pkgs["service"], ctx.svc_name, f"interface {ctx.svc_name}",
           member, ctx.func, imports, blank_default=None)

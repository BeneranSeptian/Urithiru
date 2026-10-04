"""Add the override to the repository implementation (constructor-injected datasource)."""
from .common import upsert

NAME = "repository_impl"


def run(ctx):
    def member(prop):
        if ctx.has_body:
            head = f"override suspend fun {ctx.func}(request: {ctx.req_model})"
            call = f"{prop}.{ctx.func}({ctx.req_entity}.fromModel(request))"
        else:
            head = f"override suspend fun {ctx.func}()"
            call = f"{prop}.{ctx.func}()"
        return (f"{head} = DataStateBoundResource.createNetworkCall {{\n"
                f"    {call}\n"
                f"}}.getResult()")

    ds_fq = f"{ctx.pkgs['datasource']}.{ctx.ds_name}"
    sig = f"class {ctx.repo_impl_name}(\n    private val {ctx.ds_prop}: {ctx.ds_name}\n) : {ctx.repo_name}"
    imports = [ctx.fq(ctx.req_entity), ctx.fq(ctx.req_model)] if ctx.has_body else []
    skeleton = [ds_fq, f"{ctx.pkgs['repository']}.{ctx.repo_name}"]

    upsert(ctx, NAME, ctx.repo_impl_path, ctx.pkgs["repository_impl"], ctx.repo_impl_name, sig, member,
           ctx.func, imports, ctor=(ctx.ds_name, ctx.ds_prop, ds_fq), skeleton_imports=skeleton,
           soft_imports=[ctx.cfg.imports["DataStateBoundResource"]])

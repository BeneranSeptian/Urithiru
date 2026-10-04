"""Add the override to the datasource implementation (constructor-injected service)."""
from ..names import lower_first
from .common import upsert

NAME = "datasource_impl"
MAX_LINE = 120


def run(ctx):
    m = ctx.model
    default_prop = lower_first(ctx.svc_name)

    def member(prop):
        if ctx.has_body:
            head = f"override suspend fun {ctx.func}(entity: {ctx.req_entity})"
            call = f"{prop}.{ctx.func}(Request{m}.fromEntity(entity))"
        else:
            head = f"override suspend fun {ctx.func}()"
            call = f"{prop}.{ctx.func}()"
        tail = ".toEntityWithData { it?.toEntity() }"
        one = f"{head} = {call}{tail}"
        if len(one) + 4 <= MAX_LINE:
            return one
        return f"{head} =\n    {call}\n        {tail}"

    svc_fq = f"{ctx.pkgs['service']}.{ctx.svc_name}"
    sig = f"class {ctx.impl_name}(\n    private val {default_prop}: {ctx.svc_name}\n) : {ctx.ds_name}"
    imports = [ctx.fq(f"Request{m}"), ctx.fq(ctx.req_entity)] if ctx.has_body else []
    skeleton = [f"{ctx.pkgs['datasource']}.{ctx.ds_name}", svc_fq]

    upsert(ctx, NAME, ctx.impl_path, ctx.pkgs["impl"], ctx.impl_name, sig, member, ctx.func, imports,
           ctor=(ctx.svc_name, default_prop, svc_fq), skeleton_imports=skeleton)

"""Create the use case class in paths.useCase (new file only, never appended)."""
from ..kotlin import add_imports
from .common import drop_same_package

NAME = "usecase"


def run(ctx):
    cls, m = ctx.usecase_class, ctx.model
    path, rel = ctx.usecase_path, ctx.rel(ctx.usecase_path)

    elsewhere = [p for p in ctx.index.find_decl(cls) if p != path]
    if ctx.fs.exists(path) or elsewhere:
        where = rel if ctx.fs.exists(path) else ctx.rel(elsewhere[0])
        ctx.say(NAME, f"SKIP    {cls} already exists at {where}")
        return

    params = f"request: {ctx.req_model}" if ctx.has_body else ""
    arg = "request" if ctx.has_body else ""
    text = (f"package {ctx.pkgs['usecase']}\n\n"
            f"class {cls}(private val {ctx.repo_prop}: {ctx.repo_name}) {{\n"
            f"    suspend fun invoke({params}): DataState<{m}> {{\n"
            f"        return {ctx.repo_prop}.{ctx.func}({arg})\n"
            f"    }}\n"
            f"}}\n")
    imports = [ctx.cfg.imports["DataState"], ctx.fq(m), f"{ctx.pkgs['repository']}.{ctx.repo_name}"]
    if ctx.has_body:
        imports.append(ctx.fq(ctx.req_model))
    ctx.fs.write(path, add_imports(text, drop_same_package(ctx.pkgs["usecase"], imports)))
    ctx.say(NAME, f"NEW     {rel}  ({cls})")

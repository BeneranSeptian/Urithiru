"""Validate inputs and resolve every class the generated code refers to. Writes nothing."""
from ..errors import GenError
from ..kotlin import package_of_text

NAME = "preflight"


def _resolve(ctx, name, hint):
    found = ctx.index.find_decl(name)
    if not found:
        raise GenError(f"[preflight] class '{name}' not found in the project. {hint}")
    if len(found) > 1:
        raise GenError(f"[preflight] class '{name}' is declared in several files: "
                       + ", ".join(str(ctx.rel(p)) for p in found))
    pkg = package_of_text(ctx.fs.read(found[0]))
    ctx.classes[name] = (found[0], pkg)
    ctx.debug(NAME, f"{name} -> {ctx.rel(found[0])} ({pkg})")


def _status(ctx, label, path, key, subdir):
    if ctx.fs.exists(path):
        pkg = package_of_text(ctx.fs.read(path)) or ctx.cfg.package_of(key, subdir)
        state = "EXISTS  -> append"
    else:
        pkg = ctx.cfg.package_of(key, subdir)
        state = "MISSING -> create new file"
    ctx.say(NAME, f"{label:<11} {state:<27} {ctx.rel(path)}")
    return pkg


def run(ctx):
    m = ctx.model
    run_gen = "Run gen-model first (or fix the model name)."
    _resolve(ctx, f"Response{m}", run_gen)
    _resolve(ctx, f"{m}Entity", run_gen)
    if ctx.has_body:
        _resolve(ctx, f"Request{m}", run_gen + " Use --with-request.")
        _resolve(ctx, ctx.req_entity, "Pass the right entity with --request-entity, or run gen-model "
                                      "--with-request.")
        if ctx.req_entity == f"{m}SpecEntity":
            _resolve(ctx, f"{m}Spec", run_gen + " Use --with-request.")
    ctx.say(NAME, f"{ctx.method} \"{ctx.endpoint}\"  {ctx.func}()  model={m}"
                  + (f"  body={ctx.req_entity}" if ctx.has_body else ""))
    ctx.svc_pkg = _status(ctx, "service", ctx.svc_path, "service", ctx.svc_dir)
    ctx.ds_pkg = _status(ctx, "datasource", ctx.ds_path, "dataSource", ctx.svc_dir)
    ctx.impl_pkg = _status(ctx, "impl", ctx.impl_path, "dataSourceImpl", ctx.svc_dir)
    if ctx.fs.exists(ctx.module_path):
        ctx.say(NAME, f"{'di module':<11} EXISTS  -> append{'':<9} {ctx.rel(ctx.module_path)}")
    else:
        ctx.say(NAME, f"{'di module':<11} NOT FOUND (remote_di stage will fail) {ctx.rel(ctx.module_path)}")

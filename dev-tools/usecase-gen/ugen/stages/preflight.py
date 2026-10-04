"""Validate inputs and resolve everything the selected stages refer to. Writes nothing."""
import re

from ..errors import GenError
from ..kotlin import package_of_text

NAME = "preflight"
NEEDS_REQ_MODEL = ("repository", "repository_impl", "usecase")
RUN_GEN_MODEL = "Run gen-model first (or fix the model name)."


def _needs(ctx, stage):
    m, body = ctx.model, ctx.has_body
    return {
        "service": [f"Response{m}", f"{m}Entity"] + ([f"Request{m}"] if body else []),
        "datasource": [f"{m}Entity"] + ([ctx.req_entity] if body else []),
        "datasource_impl": [f"Request{m}", ctx.req_entity] if body else [],
        "repository": [m] + ([ctx.req_model] if body else []),
        "repository_impl": [ctx.req_entity, ctx.req_model] if body else [],
        "usecase": [m] + ([ctx.req_model] if body else []),
    }.get(stage, [])


def _resolve(ctx, name):
    if name in ctx.classes:
        return
    found = ctx.index.find_decl(name)
    if not found:
        hint = RUN_GEN_MODEL + (" Use --with-request." if name.startswith("Request") or name.endswith("Spec") else "")
        if name == ctx.req_entity and name != f"{ctx.model}SpecEntity":
            hint = "Check --request-entity."
        raise GenError(f"[{NAME}] class '{name}' not found in the project. {hint}")
    if len(found) > 1:
        raise GenError(f"[{NAME}] class '{name}' is declared in several files: "
                       + ", ".join(str(ctx.rel(p)) for p in found))
    ctx.classes[name] = (found[0], package_of_text(ctx.fs.read(found[0])))
    ctx.debug(NAME, f"{name} -> {ctx.rel(found[0])} ({ctx.classes[name][1]})")


def _derive_req_model(ctx):
    """Model type that goes in repository/use case signatures for the request body."""
    if ctx.req_model:
        return
    if ctx.req_entity == f"{ctx.model}SpecEntity":
        ctx.req_model = f"{ctx.model}Spec"
        return
    found = ctx.index.find_decl(ctx.req_entity)
    if found:
        m = re.search(rf"FromModelMapper<\s*(\w+)\s*,\s*{re.escape(ctx.req_entity)}\s*>", ctx.fs.read(found[0]))
        if m:
            ctx.req_model = m.group(1)
            ctx.debug(NAME, f"request model {ctx.req_model} read from FromModelMapper in {ctx.rel(found[0])}")
            return
    if ctx.interactive:
        ctx.req_model = input(f"Request model type for {ctx.req_entity}: ").strip()
    if not ctx.req_model:
        raise GenError(f"[{NAME}] cannot tell which model converts to {ctx.req_entity} "
                       f"(no FromModelMapper<Model, {ctx.req_entity}> found). Pass --request-model.")


def _targets(ctx):
    c, t = ctx.cfg, {}
    t["service"] = (ctx.svc_path, "service", ctx.svc_dir)
    t["datasource"] = (ctx.ds_path, "dataSource", ctx.svc_dir)
    t["impl"] = (ctx.impl_path, "dataSourceImpl", ctx.svc_dir)
    if "repository" in c.paths:
        t["repository"] = (ctx.repo_path, "repository", ctx.svc_dir)
    if "repositoryImpl" in c.paths:
        t["repository_impl"] = (ctx.repo_impl_path, "repositoryImpl", ctx.svc_dir)
    if "useCase" in c.paths:
        t["usecase"] = (ctx.usecase_path, "useCase", "")
    return t


STAGE_TARGET = {"service": "service", "datasource": "datasource", "datasource_impl": "impl",
                "repository": "repository", "repository_impl": "repository_impl", "usecase": "usecase"}
STAGE_MODULE = {"remote_di": ("di module", "module_path"), "data_di": ("di module", "data_module_path")}


def run(ctx):
    sel = ctx.selected
    if ctx.has_body and any(s in sel for s in NEEDS_REQ_MODEL):
        _derive_req_model(ctx)

    seen = []
    for st in sel:
        for name in _needs(ctx, st):
            if name not in seen:
                seen.append(name)
    for name in seen:
        _resolve(ctx, name)

    if ctx.has_body and "repository_impl" in sel:
        path = ctx.classes[ctx.req_entity][0]
        if "fromModel" not in ctx.fs.read(path):
            raise GenError(f"[{NAME}] {ctx.req_entity} ({ctx.rel(path)}) has no fromModel(), so the repository "
                           f"cannot convert the request. Add a FromModelMapper companion to it.")

    ctx.say(NAME, f"{ctx.method} \"{ctx.endpoint}\"  {ctx.func}()  model={ctx.model}"
                  + (f"  body={ctx.req_model or ctx.req_entity}" if ctx.has_body else ""))

    targets = _targets(ctx)
    for label, (path, key, subdir) in targets.items():
        exists = ctx.fs.exists(path)
        ctx.pkgs[label] = (package_of_text(ctx.fs.read(path)) if exists else None) or ctx.cfg.package_of(key, subdir)
    for st in sel:
        label = STAGE_TARGET.get(st)
        if label:
            path = targets[label][0]
            state = "EXISTS  -> append" if ctx.fs.exists(path) else "MISSING -> create new file"
            if label == "usecase":
                state = "EXISTS  -> skip" if ctx.fs.exists(path) else "MISSING -> create new file"
            ctx.say(NAME, f"{label:<15} {state:<27} {ctx.rel(path)}")
        elif st == "usecase_cluster" and ctx.cluster == "" and "useCaseModule" in ctx.cfg.paths:
            path = ctx.use_case_module_path
            state = "EXISTS  -> append" if ctx.fs.exists(path) else "NOT FOUND (stage will fail)"
            ctx.say(NAME, f"{st:<15} {state:<27} {ctx.rel(path)}")
        elif st in STAGE_MODULE:
            lbl, prop = STAGE_MODULE[st]
            path = getattr(ctx, prop)
            state = "EXISTS  -> append" if ctx.fs.exists(path) else "NOT FOUND (stage will fail)"
            ctx.say(NAME, f"{st:<15} {state:<27} {ctx.rel(path)}")

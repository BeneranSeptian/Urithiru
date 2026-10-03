import argparse
import difflib
import sys

from . import config as config_mod
from .context import Ctx
from .errors import GenError
from .index import ProjectIndex
from .names import (BODY_METHODS, infer_method, parse_endpoint, parse_method, parse_service_path,
                    parse_use_case)
from .stages import PREFLIGHT, STAGES
from .vfs import VirtualFS

STAGE_NAMES = [n for n, _ in STAGES]


def ask(label, given, default=None, parse=None, interactive=True):
    """Value from the CLI flag, else prompt (re-asks on invalid input), else default."""
    if given:
        return parse(given) if parse else given
    if not interactive:
        if default:
            return parse(default) if parse else default
        raise GenError(f"{label} is required (pass it as an option).")
    while True:
        raw = input(f"{label}{f' [{default}]' if default else ''}: ").strip() or (default or "")
        if not raw:
            print("  required.")
            continue
        try:
            return parse(raw) if parse else raw
        except GenError as e:
            print(f"  {e}")


def print_diff(ctx, changes):
    for path, old, new in changes:
        rel = ctx.rel(path)
        a = old.splitlines(True) if old is not None else []
        diff = difflib.unified_diff(a, new.splitlines(True), "/dev/null" if old is None else f"a/{rel}",
                                    f"b/{rel}", n=2)
        print("\n" + "".join(l if l.endswith("\n") else l + "\n" for l in diff).rstrip("\n"))
    print()


def run(argv):
    ap = argparse.ArgumentParser(prog="gen-usecase", description="Generate service -> datasource -> impl -> DI "
                                 "for one use case. Run gen-model first so the data classes exist.")
    ap.add_argument("use_case", nargs="?", help="e.g. getTestingUseCase")
    ap.add_argument("--service", help="e.g. example/sub/ExampleService.kt (relative to paths.service)")
    ap.add_argument("--method", help="GET|POST|PUT|PATCH|DELETE (default inferred from the use case name)")
    ap.add_argument("--endpoint", help='e.g. testing/v1  (or @GET("testing/v1"))')
    ap.add_argument("--model", help="Model name used by gen-model, e.g. Example")
    ap.add_argument("--request-entity", help="Entity type for the body (default <Model>SpecEntity)")
    ap.add_argument("--only", help=f"Comma separated stages to run: {', '.join(STAGE_NAMES)}")
    ap.add_argument("--skip", help="Comma separated stages to skip")
    ap.add_argument("--stages", action="store_true", help="List the stages and exit")
    ap.add_argument("--dry-run", action="store_true", help="Show the diff, write nothing")
    ap.add_argument("--yes", "-y", action="store_true", help="Do not ask for confirmation")
    ap.add_argument("--verbose", "-v", action="store_true", help="Show every decision and always print diffs")
    ap.add_argument("--project", help="Android project root (overrides config / ANDROID_PROJECT_ROOT)")
    ap.add_argument("--config", help="Path to config.json (default: ../config.json)")
    o = ap.parse_args(argv)

    if o.stages:
        print("preflight (always)  validate input, resolve classes, report which files exist")
        for n, mod in STAGES:
            print(f"{n:<19} {(mod.__doc__ or '').strip().splitlines()[0]}")
        return

    selected = [s.strip() for s in (o.only or ",".join(STAGE_NAMES)).split(",") if s.strip()]
    skipped = {s.strip() for s in (o.skip or "").split(",") if s.strip()}
    bad = [s for s in selected + list(skipped) if s not in STAGE_NAMES]
    if bad:
        raise GenError(f"Unknown stage(s): {', '.join(bad)}. Available: {', '.join(STAGE_NAMES)}")
    run_stages = [(n, m) for n, m in STAGES if n in selected and n not in skipped]

    cfg = config_mod.load(o.config, o.project)
    tty = sys.stdin.isatty()

    use_case = ask("Use case name (e.g. getTestingUseCase)", o.use_case, interactive=tty)
    func, verb = parse_use_case(use_case)
    svc_dir, svc_name = ask("Service file (e.g. example/sub/ExampleService.kt)", o.service,
                            parse=parse_service_path, interactive=tty)
    method = ask("HTTP method", o.method, default=infer_method(verb), parse=parse_method, interactive=tty)
    ep_method, endpoint = ask("Endpoint (e.g. testing/v1)", o.endpoint, parse=parse_endpoint, interactive=tty)
    if ep_method and ep_method != method:
        raise GenError(f"Method mismatch: endpoint says @{ep_method} but the method is {method}.")
    model = ask("Model name (e.g. Example)", o.model, interactive=tty)
    req_entity = f"{model}SpecEntity"
    if method in BODY_METHODS:
        req_entity = ask("Request entity type", o.request_entity, default=req_entity, interactive=tty)

    ctx = Ctx(cfg=cfg, fs=VirtualFS(), index=ProjectIndex(cfg.root), verbose=o.verbose, use_case=use_case,
              func=func, method=method, endpoint=endpoint, model=model, svc_dir=svc_dir,
              svc_name=svc_name, req_entity=req_entity)

    print(f"\nProject: {cfg.root}\nConfig:  {cfg.path}\n")
    PREFLIGHT.run(ctx)
    for name, mod in run_stages:
        mod.run(ctx)

    changes = ctx.fs.changes()
    if not changes:
        print("\nNothing to change.")
        return
    print("\nPlan:")
    for path, old, _ in changes:
        print(f"  {'NEW   ' if old is None else 'MODIFY'}  {ctx.rel(path)}")
    if o.dry_run or o.verbose or not o.yes:
        print_diff(ctx, changes)
    if o.dry_run:
        print("Dry run: nothing was written.")
        return
    if not o.yes:
        if not tty:
            raise GenError("Not a terminal: pass --yes to apply the changes.")
        if input("Apply these changes? [y/N]: ").strip().lower() != "y":
            print("Aborted, nothing was written.")
            return
    ctx.fs.apply()
    print("Done.")


def main(argv=None):
    try:
        run(argv)
    except GenError as e:
        print(f"\nERROR: {e}", file=sys.stderr)
        sys.exit(1)
    except KeyboardInterrupt:
        print("\nAborted, nothing was written.")
        sys.exit(130)

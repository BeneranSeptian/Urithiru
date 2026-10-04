"""Register the use case: inline in UseCaseModule.kt's createList(...), or in a cluster module you name."""
import re

from ..errors import GenError
from ..kotlin import add_imports, body_range, line_indent, mask, match_pair, package_of_text
from .common import drop_same_package
from .di_common import insert_registration

NAME = "usecase_cluster"
AFTER = r"^[ \t]*(?:factory|single|viewModel)\b"


def _registered_in(ctx, cls):
    """First file that already registers the use case (`Cls(get(`, or `::Cls`)."""
    hits = ctx.index.find_text(rf"(?:\b{re.escape(cls)}\s*\(\s*get\s*\(|::\s*{re.escape(cls)}\b)")
    return hits[0] if hits else None


def _inline(ctx, cls, imp):
    path, rel = ctx.use_case_module_path, ctx.rel(ctx.use_case_module_path)
    if not ctx.fs.exists(path):
        raise GenError(f"[{NAME}] {rel} not found. Check paths.useCaseModule in config.json "
                       f"(the file must be named UseCaseModule.kt).")
    text = ctx.fs.read(path)
    masked = mask(text)
    m = re.search(r"\bcreateList\s*\(", masked)
    if not m:
        raise GenError(f"[{NAME}] no createList( call found in {rel}.")
    op = m.end() - 1
    cl = match_pair(masked, op, "(", ")")
    nl = text.find("\n", op)
    if cl < 0 or nl < 0 or nl > cl or masked[op + 1:nl].strip() != "":
        raise GenError(f"[{NAME}] {rel}: write createList( with its elements on separate lines (the first element "
                       f"must start on the line after 'createList(').")
    first = next((l for l in text[nl + 1:cl].splitlines() if l.strip()), "")
    indent = re.match(r"[ \t]*", first).group() if first else line_indent(text, m.start()) + "    "
    comma = "," if masked[op + 1:cl].strip() else ""
    element = f"module {{ factory {{ {cls}(get()) }} }}{comma}"
    text = text[:nl + 1] + indent + element + "\n" + text[nl + 1:]
    text = add_imports(text, drop_same_package(package_of_text(text) or "", [imp]))
    ctx.fs.write(path, text)
    ctx.say(NAME, f"APPEND  {rel}  (+ inline module for {cls} at the top of createList)")


def _named(ctx, cls, imp):
    name = ctx.cluster
    decl = rf"\bval\s+{re.escape(name)}\s*=\s*module\b(?=\s*\{{)"
    files = ctx.index.find_text(decl)
    if not files:
        raise GenError(f"[{NAME}] cluster module '{name}' not found. Expected `internal val {name} = module {{` "
                       f"somewhere in the project. Leave the cluster empty for an inline entry in UseCaseModule.kt, "
                       f"or use 'none' to only print the line.")
    if len(files) > 1:
        raise GenError(f"[{NAME}] cluster module '{name}' is declared in several files: "
                       + ", ".join(str(ctx.rel(p)) for p in files))
    path, rel = files[0], ctx.rel(files[0])
    text = ctx.fs.read(path)
    masked = mask(text)
    m = re.search(decl, masked)
    o, c = body_range(masked, m)
    if o < 0 or c < 0:
        raise GenError(f"[{NAME}] could not find the body of '{name}' in {rel}.")
    try:
        text, how = insert_registration(text, masked, m, o, c, f"factory {{ {cls}(get()) }}", AFTER, False)
    except GenError as e:
        raise GenError(f"[{NAME}] {rel}: {e}")
    text = add_imports(text, drop_same_package(package_of_text(text) or "", [imp]))
    ctx.fs.write(path, text)
    ctx.say(NAME, f"APPEND  {rel}  (+ {cls} in {name})")
    ctx.debug(NAME, f"inserted {how}")


def run(ctx):
    cls = ctx.usecase_class
    imp = f"{ctx.pkgs['usecase']}.{cls}"
    if ctx.cluster.lower() == "none":
        ctx.note(f"Register the use case in a module:\n    factory {{ {cls}(get()) }}\n  and import {imp}")
        ctx.say(NAME, "cluster 'none': nothing written, see the note at the end")
        return
    done = _registered_in(ctx, cls)
    if done:
        ctx.say(NAME, f"SKIP    {cls} is already registered in {ctx.rel(done)}")
        return
    (_named if ctx.cluster else _inline)(ctx, cls, imp)

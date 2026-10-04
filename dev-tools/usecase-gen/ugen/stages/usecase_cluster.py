"""Register the use case in a cluster module you name (internal val x = module { ... })."""
import re

from ..errors import GenError
from ..kotlin import body_range, mask, missing_imports
from .di_common import insert_registration

NAME = "usecase_cluster"
AFTER = r"^[ \t]*(?:factory|single|viewModel)\b"


def run(ctx):
    snippet = f"factory {{ {ctx.usecase_class}(get()) }}"
    imp = f"{ctx.pkgs['usecase']}.{ctx.usecase_class}"
    if not ctx.cluster:
        ctx.note(f"Register the use case in its cluster module:\n    {snippet}\n"
                 f"  and import {imp}\n"
                 f"  (the file is generated in {ctx.cfg.paths['useCase']}; the package changes if you move it)")
        ctx.say(NAME, "no cluster given: nothing written, see the note at the end")
        return

    decl = rf"\bval\s+{re.escape(ctx.cluster)}\s*=\s*module\b(?=\s*\{{)"
    files = ctx.index.find_text(decl)
    if not files:
        raise GenError(f"[{NAME}] cluster module '{ctx.cluster}' not found. Expected "
                       f"`internal val {ctx.cluster} = module {{` somewhere in the project. "
                       f"Leave the cluster empty to only print the line.")
    if len(files) > 1:
        raise GenError(f"[{NAME}] cluster module '{ctx.cluster}' is declared in several files: "
                       + ", ".join(str(ctx.rel(p)) for p in files))
    path, rel = files[0], ctx.rel(files[0])
    text = ctx.fs.read(path)
    masked = mask(text)
    m = re.search(decl, masked)
    o, c = body_range(masked, m)
    if o < 0 or c < 0:
        raise GenError(f"[{NAME}] could not find the body of '{ctx.cluster}' in {rel}.")
    if re.search(rf"\b{re.escape(ctx.usecase_class)}\b", masked[o + 1:c]):
        ctx.say(NAME, f"SKIP    {ctx.usecase_class} is already registered in {ctx.cluster}")
        return
    try:
        text, how = insert_registration(text, masked, m, o, c, snippet, AFTER, False)
    except GenError as e:
        raise GenError(f"[{NAME}] {rel}: {e}")
    ctx.fs.write(path, text)
    ctx.say(NAME, f"APPEND  {rel}  (+ {ctx.usecase_class} in {ctx.cluster})")
    ctx.debug(NAME, f"inserted {how}")
    missing = missing_imports(text, [imp])
    if missing:
        ctx.warn_imports(NAME, path, missing)
        ctx.warnings[-1] += f"\n      (the package changes if you move {ctx.usecase_class}.kt)"

"""Shared logic for Koin-style registrations (RemoteModule, DataModule, use case clusters)."""
import re

from ..errors import GenError
from ..kotlin import (append_members, body_range, indent_block, line_indent, mask, missing_imports)


def statement_end(masked, start):
    """Index of the newline that ends the statement starting on the line at `start`."""
    depth = 0
    for i in range(start, len(masked)):
        ch = masked[i]
        if ch in "({":
            depth += 1
        elif ch in ")}":
            depth -= 1
        elif ch == "\n" and depth <= 0:
            return i
    return len(masked)


def insert_registration(text, masked, m, o, c, snippet, after, blank):
    """New lines go below the LAST line matching `after`; else at the end of the block."""
    if after:
        hits = list(re.finditer(after, masked[o + 1:c], re.M))
        if hits:
            ls = masked.rfind("\n", 0, o + 1 + hits[-1].start()) + 1
            end = statement_end(masked, ls)
            ind = line_indent(text, ls)
            return text[:end] + "\n" + indent_block(snippet, ind) + text[end:], "below the last matching line"
    return append_members(text, masked, m, o, c, snippet, blank), "at the end of the block"


def register(ctx, stage, path, items):
    """items: [{label, spec, imports}] where spec has template/detect/after/blank (see config.DEFAULT_DI)."""
    rel = ctx.rel(path)
    if not ctx.fs.exists(path):
        raise GenError(f"[{stage}] {rel} not found. Check the path in config.json.")
    text = ctx.fs.read(path)
    container = ctx.cfg.di["container"]
    added, imports = 0, []

    for it in items:
        spec = it["spec"]
        masked = mask(text)
        m = re.search(container, masked)
        if not m:
            raise GenError(f"[{stage}] no module declaration matching di.container /{container}/ found in {rel}.")
        o, c = body_range(masked, m)
        if o < 0 or c < 0:
            raise GenError(f"[{stage}] could not find the module body in {rel}.")
        hit = next((p for p in spec["detect"] if re.search(ctx.fill(p), masked)), None)
        if hit:
            ctx.say(stage, f"SKIP    {it['label']} is already registered in {rel}")
            ctx.debug(stage, f"matched /{ctx.fill(hit)}/")
            continue
        try:
            text, how = insert_registration(text, masked, m, o, c, ctx.fill(spec["template"]),
                                            spec.get("after"), spec.get("blank", False))
        except GenError as e:
            raise GenError(f"[{stage}] {rel}: {e}")
        ctx.debug(stage, f"{it['label']}: inserted {how}")
        added += 1
        imports += it["imports"]

    if not added:
        return
    ctx.fs.write(path, text)
    ctx.say(stage, f"APPEND  {rel}  (+ {added} registration(s))")
    missing = missing_imports(text, imports)
    if missing:
        ctx.warn_imports(stage, path, missing)

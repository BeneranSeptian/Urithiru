"""Register the service and the datasource in RemoteModule.kt (snippets/markers come from config 'di')."""
import re

from ..errors import GenError
from ..kotlin import (add_imports, append_members, body_range, indent_block, line_indent, mask,
                      package_of_text)
from ..names import lower_first
from .common import drop_same_package

NAME = "remote_di"


def _fill(s, ctx):
    for k, v in {"{DataSourceImpl}": ctx.impl_name, "{DataSource}": ctx.ds_name,
                 "{Service}": ctx.svc_name, "{service}": lower_first(ctx.svc_name)}.items():
        s = s.replace(k, v)
    return s


def _statement_end(masked, start):
    """End (index of the newline) of the statement that starts on the line at `start`."""
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


def _insert(text, masked, m, o, c, snippet, spec):
    """Insert one binding. Order: above the marker comment, else after the last matching line, else at the end."""
    marker = spec.get("marker")
    if marker:
        mk = re.search(marker, text[o + 1:c], re.I)
        if mk:
            at = o + 1 + mk.start()
            ls = text.rfind("\n", 0, at) + 1
            ind = line_indent(text, at)
            return text[:ls] + indent_block(snippet, ind) + "\n" + text[ls:], "above the marker comment"
    after = spec.get("after")
    if after:
        hits = list(re.finditer(after, masked[o + 1:c]))
        if hits:
            at = o + 1 + hits[-1].start()
            ls = masked.rfind("\n", 0, at) + 1
            end = _statement_end(masked, ls)
            ind = line_indent(text, at)
            return text[:end] + "\n" + indent_block(snippet, ind) + text[end:], "after the last matching line"
    return (append_members(text, masked, m, o, c, snippet, spec.get("blank", False)),
            "at the end of the module")


def run(ctx):
    path = ctx.module_path
    if not ctx.fs.exists(path):
        raise GenError(f"[{NAME}] {ctx.rel(path)} not found. Check paths.remoteModule in config.json "
                       f"(the file must be named RemoteModule.kt).")
    text = ctx.fs.read(path)
    di = ctx.cfg.di

    added = 0
    for key, label in (("service", ctx.svc_name), ("dataSource", ctx.ds_name)):
        spec = di[key]
        masked = mask(text)
        m = re.search(di["container"], masked)
        if not m:
            raise GenError(f"[{NAME}] no module declaration matching di.container /{di['container']}/ "
                           f"found in {ctx.rel(path)}.")
        o, c = body_range(masked, m)
        if o < 0 or c < 0:
            raise GenError(f"[{NAME}] could not find the module body in {ctx.rel(path)}.")
        hit = next((p for p in spec["detect"] if re.search(_fill(p, ctx), masked)), None)
        if hit:
            ctx.say(NAME, f"SKIP    {label} is already registered in {ctx.rel(path)}")
            ctx.debug(NAME, f"matched /{_fill(hit, ctx)}/")
            continue
        text, how = _insert(text, masked, m, o, c, _fill(spec["template"], ctx), spec)
        ctx.debug(NAME, f"{label}: inserted {how}")
        added += 1
    if not added:
        return

    pkg = package_of_text(text) or ""
    imports = list(di.get("imports", [])) + [f"{ctx.svc_pkg}.{ctx.svc_name}",
                                              f"{ctx.ds_pkg}.{ctx.ds_name}",
                                              f"{ctx.impl_pkg}.{ctx.impl_name}"]
    ctx.fs.write(path, add_imports(text, drop_same_package(pkg, imports)))
    ctx.say(NAME, f"APPEND  {ctx.rel(path)}  (+ {added} registration(s))")

"""Small dependency-free helpers for editing Kotlin source text safely.

Everything that *searches* works on a masked copy of the text (strings and comments
replaced by spaces, same length), so braces or keywords inside them are never matched.
Indexes found in the masked text are valid in the original text.
"""
import re

PACKAGE_RE = re.compile(r"^package\s+([\w.]+)", re.M)
IMPORT_LINE = re.compile(r"^import\s+(\S+)")
_CHAR_LIT = re.compile(r"'(?:\\.[^']*|[^\\'])'")


def mask(text):
    out = list(text)
    n = len(text)
    i = 0

    def blank(a, b):
        for k in range(a, b):
            if out[k] != "\n":
                out[k] = " "

    while i < n:
        if text.startswith("//", i):
            j = text.find("\n", i)
            j = n if j < 0 else j
            blank(i, j)
            i = j
        elif text.startswith("/*", i):
            depth, j = 1, i + 2
            while j < n and depth:
                if text.startswith("/*", j):
                    depth, j = depth + 1, j + 2
                elif text.startswith("*/", j):
                    depth, j = depth - 1, j + 2
                else:
                    j += 1
            blank(i, j)
            i = j
        elif text.startswith('"""', i):
            j = text.find('"""', i + 3)
            j = n if j < 0 else j + 3
            blank(i, j)
            i = j
        elif text[i] == '"':
            j = i + 1
            while j < n and text[j] not in '"\n':
                j += 2 if text[j] == "\\" else 1
            j = min(j + 1, n)
            blank(i, j)
            i = j
        elif text[i] == "'":
            m = _CHAR_LIT.match(text, i)
            if m:
                blank(i, m.end())
                i = m.end()
            else:
                i += 1
        else:
            i += 1
    return "".join(out)


def package_of_text(text):
    m = PACKAGE_RE.search(text)
    return m.group(1) if m else None


def line_indent(text, idx):
    start = text.rfind("\n", 0, idx) + 1
    return re.match(r"[ \t]*", text[start:]).group()


def indent_block(block, indent):
    return "\n".join(indent + l if l.strip() else l for l in block.split("\n"))


def find_decl(masked, name, kinds=("interface", "class", "object")):
    return re.search(rf"\b(?:{'|'.join(kinds)})\s+{re.escape(name)}\b", masked)


def match_pair(masked, open_idx, o, c):
    depth = 0
    for i in range(open_idx, len(masked)):
        if masked[i] == o:
            depth += 1
        elif masked[i] == c:
            depth -= 1
            if depth == 0:
                return i
    return -1


def find_body(masked, start):
    """Index of the '{' that opens the body of a declaration whose header starts at `start`."""
    depth = 0
    for i in range(start, len(masked)):
        c = masked[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
        elif c == "{" and depth == 0:
            return i
    return -1


def body_range(masked, decl_match):
    o = find_body(masked, decl_match.end())
    if o < 0:
        return -1, -1
    return o, match_pair(masked, o, "{", "}")


def has_fun(masked_body, name):
    return re.search(rf"\bfun\s+(?:<[^>]*>\s*)?(?:[\w.]+\.)?{re.escape(name)}\s*\(", masked_body) is not None


def append_members(text, masked, decl_match, open_idx, close_idx, block, blank_default):
    """Insert `block` (unindented) as the last member(s) of the body, before the closing brace."""
    decl_indent = line_indent(text, decl_match.start())
    body = text[open_idx + 1:close_idx]
    if body.strip() == "":
        new = "\n" + indent_block(block, decl_indent + "    ") + "\n" + decl_indent
        return text[:open_idx + 1] + new + text[close_idx:]
    first = next((l for l in body.splitlines() if l.strip()), "")
    indent = re.match(r"[ \t]*", first).group() or decl_indent + "    "
    if len(re.findall(r"\bfun\b", masked[open_idx + 1:close_idx])) >= 2:
        blank = re.search(r"\n[ \t]*\n", body.strip()) is not None
    else:
        blank = blank_default
    new = body.rstrip() + "\n" + ("\n" if blank else "") + indent_block(block, indent) + "\n" + decl_indent
    return text[:open_idx + 1] + new + text[close_idx:]


def ensure_ctor_param(text, masked, decl_match, type_name, default_prop):
    """Make sure the class constructor has a `val x: TypeName` parameter. Returns (text, prop_name)."""
    i = decl_match.end()
    j = i
    while j < len(masked) and masked[j] in " \t":
        j += 1
    decl_indent = line_indent(text, decl_match.start())
    param = f"private val {default_prop}: {type_name}"
    if j < len(masked) and masked[j] == "(":
        close = match_pair(masked, j, "(", ")")
        params = text[j + 1:close]
        m = re.search(rf"\b(?:val|var)\s+(\w+)\s*:\s*{re.escape(type_name)}\b", masked[j + 1:close])
        if m:
            return text, m.group(1)
        if params.strip() == "":
            return text[:j] + f"(\n{decl_indent}    {param}\n{decl_indent})" + text[close + 1:], default_prop
        if "\n" in params:
            first = next(l for l in params.splitlines() if l.strip())
            ind = re.match(r"[ \t]*", first).group()
            head = params.rstrip()
            comma = "" if head.endswith(",") else ","
            new_params = f"{head}{comma}\n{ind}{param}\n{decl_indent}"
        else:
            new_params = f"{params.rstrip()}, {param}"
        return text[:j + 1] + new_params + text[close:], default_prop
    return text[:i] + f"(\n{decl_indent}    {param}\n{decl_indent})" + text[i:], default_prop


def add_imports(text, new):
    """Add missing imports in alphabetical position; never duplicates, honours `pkg.*`."""
    lines = text.split("\n")
    existing = {m.group(1) for l in lines if (m := IMPORT_LINE.match(l))}
    for imp in sorted(set(new)):
        if imp in existing or imp.rsplit(".", 1)[0] + ".*" in existing:
            continue
        idxs = [k for k, l in enumerate(lines) if IMPORT_LINE.match(l)]
        if idxs:
            pos = idxs[-1] + 1
            for k in idxs:
                if IMPORT_LINE.match(lines[k]).group(1) > imp:
                    pos = k
                    break
            lines.insert(pos, f"import {imp}")
        else:
            pk = next((k for k, l in enumerate(lines) if l.startswith("package ")), -1)
            if pk >= 0:
                lines[pk + 1:pk + 1] = ["", f"import {imp}"]
                at = pk + 2
            else:
                lines.insert(0, f"import {imp}")
                at = 0
            if at + 1 < len(lines) and lines[at + 1].strip():
                lines.insert(at + 1, "")
        existing.add(imp)
    return "\n".join(lines)

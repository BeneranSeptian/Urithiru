from ..errors import GenError
from ..kotlin import (add_imports, append_members, body_range, ensure_ctor_param, find_decl,
                      has_fun, indent_block, mask)


def drop_same_package(pkg, imports):
    return sorted({i for i in imports if i and i.rsplit(".", 1)[0] != pkg})


def upsert(ctx, stage, path, pkg, name, sig, member, fun, imports, blank_default, ctor=None):
    """Create `path` with a new declaration, or append `member` to the existing declaration `name`.

    Never overwrites a file, never duplicates a declaration or a function.
      sig     header of a NEW declaration, e.g. "interface FooService"
      member  text (or callable(prop_name) -> text) of the function(s) to add, unindented
      ctor    (TypeName, default_prop) -> make sure the class constructor injects that type
    """
    fs = ctx.fs
    imports = drop_same_package(pkg, imports)
    rel = ctx.rel(path)

    if not fs.exists(path):
        elsewhere = [p for p in ctx.index.find_decl(name) if p != path]
        elsewhere += [p for p in ctx.index.find_file(path.stem) if p != path and p not in elsewhere]
        if elsewhere:
            raise GenError(f"[{stage}] '{name}' would be created at {rel}, but it already exists at "
                           f"{ctx.rel(elsewhere[0])}. Check the path you typed.")
        prop = ctor[1] if ctor else None
        block = member(prop) if callable(member) else member
        text = f"package {pkg}\n\n{sig} {{\n{indent_block(block, '    ')}\n}}\n"
        fs.write(path, add_imports(text, imports))
        ctx.say(stage, f"NEW     {rel}  (+ {name}.{fun})")
        return

    text = fs.read(path)
    masked = mask(text)
    m = find_decl(masked, name)
    if not m:
        raise GenError(f"[{stage}] {rel} exists but does not declare '{name}'.")
    o, c = body_range(masked, m)
    if o < 0 or c < 0:
        raise GenError(f"[{stage}] could not find the body of '{name}' in {rel}.")
    if has_fun(masked[o + 1:c], fun):
        ctx.say(stage, f"SKIP    {rel}: '{name}' already has a function '{fun}'")
        return

    prop = None
    if ctor:
        new_text, prop = ensure_ctor_param(text, masked, m, ctor[0], ctor[1])
        if new_text != text:
            ctx.debug(stage, f"added constructor parameter '{prop}: {ctor[0]}' to {name}")
            text, masked = new_text, mask(new_text)
            m = find_decl(masked, name)
            o, c = body_range(masked, m)
        else:
            ctx.debug(stage, f"constructor already injects {ctor[0]} as '{prop}'")
    block = member(prop) if callable(member) else member
    text = append_members(text, masked, m, o, c, block, blank_default)
    fs.write(path, add_imports(text, imports))
    ctx.say(stage, f"APPEND  {rel}  (+ {name}.{fun})")

from ..errors import GenError
from ..kotlin import (add_imports, append_members, body_range, ensure_ctor_param, find_decl, has_fun,
                      indent_block, mask, missing_imports)


def drop_same_package(pkg, imports):
    return sorted({i for i in imports if i and i.rsplit(".", 1)[0] != pkg})


def upsert(ctx, stage, path, pkg, name, sig, member, fun, imports, blank_default, ctor=None):
    """Create `path` with a new declaration, or append `member` to the existing declaration `name`.

    New file: written with all needed imports.
    Existing file: only lines are added (plus, if `ctor` is given and the class does not inject that
    type yet, one constructor parameter); NO imports are added, missing ones are reported as warnings.
    Never overwrites a file, never duplicates a declaration or a function.
      sig     header of a NEW declaration, e.g. "interface FooService"
      member  text (or callable(prop_name) -> text) of the function(s) to add, unindented
      blank_default  True/False, or None = blank line only when the member spans several lines
      ctor    (TypeName, default_prop) -> the class constructor must inject that type
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
            ctx.warn(stage, f"{rel}: added constructor parameter 'private val {prop}: {ctor[0]}' to the existing "
                            f"class {name}. Check its DI registration, it may need one more get().")
            text, masked = new_text, mask(new_text)
            m = find_decl(masked, name)
            o, c = body_range(masked, m)
        else:
            ctx.debug(stage, f"constructor already injects {ctor[0]} as '{prop}'")
    block = member(prop) if callable(member) else member
    if blank_default is None:                # auto: blank line between multi-line members only
        blank_default = "\n" in block
    try:
        text = append_members(text, masked, m, o, c, block, blank_default)
    except GenError as e:
        raise GenError(f"[{stage}] {rel}: {e}")
    fs.write(path, text)
    ctx.say(stage, f"APPEND  {rel}  (+ {name}.{fun})")
    missing = missing_imports(text, imports)
    if missing:
        ctx.warn_imports(stage, path, missing)

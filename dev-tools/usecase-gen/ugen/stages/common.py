from ..errors import GenError
from ..kotlin import (add_imports, append_members, body_range, ensure_ctor_param, find_decl, has_fun,
                      indent_block, mask, missing_imports)


def drop_same_package(pkg, imports):
    return sorted({i for i in imports if i and i.rsplit(".", 1)[0] != pkg})


def upsert(ctx, stage, path, pkg, name, sig, member, fun, imports, blank_default=None, ctor=None,
           skeleton_imports=(), soft_imports=()):
    """Create `path` with a new declaration, or append `member` to the existing declaration `name`.

    Imports come in three kinds:
      imports           project classes (and http annotations) the NEW MEMBER uses: added to the file
                        when missing, new or existing (an existing file only gains import lines)
      skeleton_imports  classes only the new class skeleton needs (supertype, constructor type):
                        new files only
      soft_imports      base classes from config.json: new files get them, existing files only get
                        a warning listing the ones that are not covered

    Existing files only gain lines (plus one constructor parameter if `ctor` is given and the class does
    not inject that type yet). Never overwrites a file, never duplicates a declaration or a function.
      sig             header of a NEW declaration, e.g. "interface FooService"
      member          text (or callable(prop_name) -> text) of the function(s) to add, unindented
      blank_default   True/False, or None = blank line only when the member spans several lines
      ctor            (TypeName, default_prop, fully_qualified_type) the constructor must inject
    """
    fs = ctx.fs
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
        fs.write(path, add_imports(text, drop_same_package(pkg, [*imports, *skeleton_imports, *soft_imports])))
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

    prop, extra = None, []
    if ctor:
        new_text, prop = ensure_ctor_param(text, masked, m, ctor[0], ctor[1])
        if new_text != text:
            ctx.warn(stage, f"{rel}: added constructor parameter 'private val {prop}: {ctor[0]}' to the existing "
                            f"class {name}. Check its DI registration, it may need one more get().")
            extra.append(ctor[2])
            text, masked = new_text, mask(new_text)
            m = find_decl(masked, name)
            o, c = body_range(masked, m)
        else:
            ctx.debug(stage, f"constructor already injects {ctor[0]} as '{prop}'")
    block = member(prop) if callable(member) else member
    if blank_default is None:
        blank_default = "\n" in block
    try:
        text = append_members(text, masked, m, o, c, block, blank_default)
    except GenError as e:
        raise GenError(f"[{stage}] {rel}: {e}")
    before = text
    text = add_imports(text, drop_same_package(pkg, [*imports, *extra]))
    if text != before:
        ctx.debug(stage, "added missing imports: " + ", ".join(drop_same_package(pkg, [*imports, *extra])))
    fs.write(path, text)
    ctx.say(stage, f"APPEND  {rel}  (+ {name}.{fun})")
    missing = missing_imports(text, drop_same_package(pkg, soft_imports))
    if missing:
        ctx.warn_imports(stage, path, missing)

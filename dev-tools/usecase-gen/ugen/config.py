import json
import os
import re
from pathlib import Path

from .errors import GenError

TOOL_DIR = Path(__file__).resolve().parent.parent

# Koin defaults. Override any key under "di" in config.json (see README for a Hilt example).
# Placeholders: {Service} {service} {DataSource} {DataSourceImpl} {Repository} {RepositoryImpl}
# Per binding:
#   template  the Kotlin to insert
#   detect    regexes; if any matches the file the binding already exists -> skipped
#   after     regex (multiline); the new line goes below the LAST line that matches
#   blank     blank line between members when there is no match and it is appended at the end
# Existing files never get new imports; missing ones are reported as warnings.
DEFAULT_DI = {
    "container": r"\bmodule\b(?=\s*\{)",
    "service": {
        "template": "factory { createService<{Service}>(get()) }",
        "detect": [r"createService<\s*{Service}\s*>"],
        "after": r"createService<",
        "blank": False,
    },
    "dataSource": {
        "template": "factory<{DataSource}> { {DataSourceImpl}(get()) }",
        "detect": [r"<\s*{DataSource}\s*>"],
        "after": r"<\w*DataSource>\s*\{",
        "blank": False,
    },
    "repository": {
        "template": "factory<{Repository}> { {RepositoryImpl}(get()) }",
        "detect": [r"<\s*{Repository}\s*>"],
        "after": r"<\w*Repository>\s*\{",
        "blank": False,
    },
}

BASE_PATHS = ["service", "dataSource", "dataSourceImpl"]


class Config:
    def __init__(self, path, raw, root):
        self.path, self.raw, self.root = path, raw, root
        self.paths = raw.get("paths", {})
        self.imports = raw.get("imports", {})
        self.di = {**DEFAULT_DI, **raw.get("di", {})}
        self.naming = {"dataSourceSuffix": "RemoteDataSource", **raw.get("naming", {})}

    def require(self, paths=(), imports=()):
        missing = [f"paths.{k}" for k in paths if k not in self.paths]
        missing += [f"imports.{k}" for k in imports if k not in self.imports]
        if missing:
            raise GenError(f"{self.path} is missing: " + ", ".join(dict.fromkeys(missing)))

    def dir_of(self, key, subdir=""):
        return self.root / self.paths[key] / subdir

    def package_of(self, key, subdir=""):
        m = re.search(r"/(?:java|kotlin)/(.+)$", "/" + Path(self.paths[key]).as_posix())
        if not m:
            raise GenError(f"Cannot derive a package from paths.{key} = '{self.paths[key]}' "
                           f"(expected it to contain /java/ or /kotlin/)")
        pkg = m.group(1).replace("/", ".")
        return pkg + ("." + subdir.replace("/", ".") if subdir else "")


def find_config(explicit=None):
    for c in (explicit, os.environ.get("DEV_TOOLS_CONFIG"), TOOL_DIR.parent / "config.json", TOOL_DIR / "config.json"):
        if c and Path(c).expanduser().is_file():
            return Path(c).expanduser()
    raise GenError("config.json not found. Expected at ../config.json (next to the tool folders), "
                   "or pass --config / set DEV_TOOLS_CONFIG.")


def load(explicit=None, project=None):
    path = find_config(explicit)
    try:
        raw = json.loads(path.read_text())
    except json.JSONDecodeError as e:
        raise GenError(f"{path}: invalid JSON (line {e.lineno}, column {e.colno}): {e.msg}")
    root_str = project or os.environ.get("ANDROID_PROJECT_ROOT") or raw.get("projectRoot")
    root = Path(root_str or "").expanduser().resolve()
    if not root_str or not root.is_dir():
        raise GenError(f"Android project root not found: {root_str!r}\n"
                       f"Set projectRoot in {path} or pass --project /path/to/project")
    cfg = Config(path, raw, root)
    cfg.require(BASE_PATHS)
    return cfg

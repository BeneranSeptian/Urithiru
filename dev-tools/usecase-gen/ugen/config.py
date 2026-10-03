import json
import os
import re
from pathlib import Path

from .errors import GenError

TOOL_DIR = Path(__file__).resolve().parent.parent

# Koin defaults. Override any key under "di" in config.json (see README for a Hilt example).
# Placeholders: {Service} {service} {DataSource} {DataSourceImpl}
# Per binding:
#   template  the Kotlin to insert
#   detect    regexes; if any matches the file the binding already exists -> skipped
#   marker    regex of a comment line; the binding is inserted ABOVE it (marker is kept)
#   after     regex; used when there is no marker: insert after the LAST line that matches
#   blank     blank line between members when appended at the end of the module
DEFAULT_DI = {
    "container": r"\bmodule\b(?=\s*\{)",
    "imports": [],
    "service": {
        "template": "factory { createService<{Service}>(get()) }",
        "detect": [r"createService<\s*{Service}\s*>"],
        "marker": r"//\s*new\s+service\b",
        "after": r"createService<",
        "blank": False,
    },
    "dataSource": {
        "template": "factory<{DataSource}> { {DataSourceImpl}(get()) }",
        "detect": [r"<\s*{DataSource}\s*>"],
        "marker": r"//\s*new\s+data\s*source\b",
        "after": r"<\w*DataSource>\s*\{",
        "blank": False,
    },
}

REQUIRED_PATHS = ["response", "request", "entity", "model", "service", "dataSource",
                  "dataSourceImpl", "remoteModule"]
REQUIRED_IMPORTS = ["BaseResponse", "ResponseEntity", "toEntityWithData"]


class Config:
    def __init__(self, path, raw, root):
        self.path, self.raw, self.root = path, raw, root
        self.paths = raw["paths"]
        self.imports = raw["imports"]
        self.di = {**DEFAULT_DI, **raw.get("di", {})}
        self.naming = {"dataSourceSuffix": "RemoteDataSource", **raw.get("naming", {})}

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
    missing = [f"paths.{k}" for k in REQUIRED_PATHS if k not in raw.get("paths", {})]
    missing += [f"imports.{k}" for k in REQUIRED_IMPORTS if k not in raw.get("imports", {})]
    if missing:
        raise GenError(f"{path} is missing: " + ", ".join(missing))
    return Config(path, raw, root)

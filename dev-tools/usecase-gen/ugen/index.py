"""Project-wide index of top-level declarations and file names (for duplicate / existence checks)."""
import os
import re
from pathlib import Path

SKIP_DIRS = {"build", ".git", ".gradle", ".idea", "node_modules"}
DECL_RE = re.compile(
    r"^(?:(?:public|internal|private|protected|abstract|open|sealed|data|enum|annotation|inline|value|fun)\s+)*"
    r"(?:class|interface|object)\s+(\w+)", re.M)


class ProjectIndex:
    def __init__(self, root):
        self.root = root
        self._decls, self._files = None, None

    def _build(self):
        self._decls, self._files = {}, {}
        for dp, dns, fns in os.walk(self.root):
            dns[:] = [d for d in dns if d not in SKIP_DIRS]
            for fn in fns:
                if not fn.endswith(".kt"):
                    continue
                p = Path(dp) / fn
                self._files.setdefault(p.stem, []).append(p)
                try:
                    text = p.read_text(errors="ignore")
                except OSError:
                    continue
                for m in DECL_RE.finditer(text):
                    self._decls.setdefault(m.group(1), []).append(p)

    def find_decl(self, name):
        if self._decls is None:
            self._build()
        return list(self._decls.get(name, []))

    def find_file(self, stem):
        if self._files is None:
            self._build()
        return list(self._files.get(stem, []))

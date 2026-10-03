"""In-memory file layer: stages read/write here, nothing touches disk until apply()."""


class VirtualFS:
    def __init__(self):
        self._orig, self._new = {}, {}

    def exists(self, p):
        return p in self._new or p.is_file()

    def read(self, p):
        return self._new[p] if p in self._new else p.read_text()

    def write(self, p, text):
        if p not in self._orig:
            self._orig[p] = p.read_text() if p.is_file() else None
        self._new[p] = text

    def changes(self):
        return [(p, self._orig[p], t) for p, t in self._new.items() if self._orig[p] != t]

    def apply(self):
        for p, _, text in self.changes():
            p.parent.mkdir(parents=True, exist_ok=True)
            p.write_text(text)

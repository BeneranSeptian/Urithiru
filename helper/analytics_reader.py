#!/usr/bin/env python3
"""Prints a compact summary of a tracking-spec JSON file, sized to fit a terminal screenshot.

  python3 analytics_reader.py <file.json>            # summary (2 screens)
  python3 analytics_reader.py <file.json> --full     # also writes json_report.txt with everything

Standard library only. Reads the file locally; nothing is sent anywhere.
"""
import json
import re
import sys
from collections import Counter, OrderedDict

W = 100  # output width


def camel_tokens(s):
    return re.findall(r"[A-Z]+(?=[A-Z][a-z]|\d|$)|[A-Z]?[a-z]+|\d+", s)


def head(title):
    print("\n" + ("── " + title + " ").ljust(W, "─"))


def table(rows, cols, limit=None):
    rows = rows[:limit] if limit else rows
    widths = [max(len(str(c)), *(len(str(r[i])) for r in rows)) if rows else len(str(c)) for i, c in enumerate(cols)]
    print("  " + "  ".join(str(c).ljust(w) for c, w in zip(cols, widths)))
    for r in rows:
        print("  " + "  ".join(str(v).ljust(w) for v, w in zip(r, widths)))


def short(s, n):
    s = str(s)
    return s if len(s) <= n else s[: n - 1] + "…"


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    path, full = sys.argv[1], "--full" in sys.argv
    with open(path, encoding="utf-8") as fh:
        raw = fh.read()
    data = json.loads(raw, object_pairs_hook=OrderedDict)
    lines_out = []

    # ---- 1. shape ---------------------------------------------------------------------------
    head("1. FILE")
    print(f"  file size {len(raw) / 1024:.0f} KB · {raw.count(chr(10)) + 1} lines · top level: {"object" if isinstance(data, dict) else type(data).__name__}")
    if not isinstance(data, dict):
        sys.exit("  top level is not an object; send a photo of the first 30 lines instead")
    keys = list(data)
    non_dict = [k for k in keys if not isinstance(data[k], dict)]
    print(f"  {len(keys)} top-level keys · {len(non_dict)} whose value is not an object"
          + (f" (e.g. {', '.join(non_dict[:3])})" if non_dict else ""))

    # ---- 2. key naming --------------------------------------------------------------------------
    head("2. KEY PREFIXES (first lowercase word of each key)")
    pref = Counter()
    pref_ex = {}
    for k in keys:
        m = re.match(r"^[a-z]+", k)
        p = m.group(0) if m else "(none)"
        pref[p] += 1
        pref_ex.setdefault(p, k)
    table([(p, n, short(pref_ex[p], 60)) for p, n in pref.most_common()], ["prefix", "keys", "example"], 15)
    if len(pref) > 15:
        print(f"  … {len(pref) - 15} more prefixes")

    # ---- 3. tracker sections ----------------------------------------------------------------------
    head("3. TRACKER SECTIONS (second-level keys)")
    sec = Counter()
    combos = Counter()
    fields = Counter()
    other_types = Counter()
    for k in keys:
        v = data[k]
        if not isinstance(v, dict):
            continue
        combos[" + ".join(v.keys()) or "(empty)"] += 1
        for t, s in v.items():
            sec[t] += 1
            if isinstance(s, dict):
                for f in s:
                    fields[f"{f}"] += 1
            else:
                other_types[f"{t}={type(s).__name__}"] += 1
    table([(t, n, f"{n / len(keys) * 100:.0f}%") for t, n in sec.most_common()], ["section", "keys", "share"])
    print("\n  fields inside sections: " + ", ".join(f"{f} ({n})" for f, n in fields.most_common()))
    if other_types:
        print("  sections that are not objects: " + ", ".join(f"{t} ({n})" for t, n in other_types.most_common(5)))
    print("\n  most common combinations:")
    table([(n, short(c, 80)) for c, n in combos.most_common()], ["keys", "sections"], 8)

    # ---- 4. parameters -------------------------------------------------------------------------------
    head("4. PARAMETERS")
    with_params = Counter()
    logical = Counter()
    pfield = Counter()
    identity = Counter()
    per_key = Counter()
    for k in keys:
        v = data[k]
        if not isinstance(v, dict):
            continue
        n_key = 0
        for t, s in v.items():
            pm = s.get("parameterMap") if isinstance(s, dict) else None
            if isinstance(pm, dict) and pm:
                with_params[t] += 1
                n_key = max(n_key, len(pm))
                for lg, fld in pm.items():
                    logical[lg] += 1
                    pfield[str(fld)] += 1
                    if lg == fld:
                        identity[t] += 1
        per_key[n_key] += 1
    print("  sections with a parameterMap: " + ", ".join(f"{t} {n}" for t, n in with_params.most_common()))
    print("  params per key (max over trackers): " + ", ".join(f"{n} params: {c}" for n, c in sorted(per_key.items())))
    if identity:
        print("  mappings where left side == right side: "
              + ", ".join(f"{t} {n}" for t, n in identity.most_common()))
    print("\n  top logical keys (left side)          top field names (right side)")
    lt, ft = logical.most_common(12), pfield.most_common(12)
    for i in range(max(len(lt), len(ft))):
        a = f"{short(lt[i][0], 26):<26} {lt[i][1]:>4}" if i < len(lt) else ""
        b = f"{short(ft[i][0], 30):<30} {ft[i][1]:>4}" if i < len(ft) else ""
        print(f"  {a:<38} {b}")

    # ---- 5. names --------------------------------------------------------------------------------------
    head("5. EVENT NAMES")
    names_by_t = {}
    for k in keys:
        v = data[k]
        if not isinstance(v, dict):
            continue
        for t, s in v.items():
            if isinstance(s, dict):
                names_by_t.setdefault(t, []).append((k, s.get("name")))
    rows = []
    for t, lst in names_by_t.items():
        nm = [n for _, n in lst if isinstance(n, str)]
        c = Counter(nm)
        rows.append((t, len(lst), len(lst) - len(nm), sum(1 for n in nm if not n.strip()),
                     sum(1 for n in nm if "%@" in n or "%s" in n), sum(1 for n in nm if " " in n),
                     max((len(n) for n in nm), default=0), sum(1 for n in nm if len(n) > 40),
                     sum(1 for n in nm if not re.match(r"^[A-Za-z][A-Za-z0-9_]*$", n)),
                     sum(v for v in c.values() if v > 1)))
    table(rows, ["section", "names", "missing", "empty", "%@/%s", "spaces", "longest", ">40 chars",
                 "not [A-Za-z0-9_]", "shared by >1 key"])

    # ---- 6. clustering hints -------------------------------------------------------------------------
    head("6. CLUSTERING HINTS")
    def split(k):
        m = re.match(r"^([a-z]+)(.*)$", k)
        return (m.group(1), m.group(2)) if m else ("(none)", k)
    by_pref = {}
    for k in keys:
        p, rest = split(k)
        by_pref.setdefault(p, []).append(rest)
    print("  for each prefix: how many of its keys continue with the full remainder of a key from another prefix")
    for p, rests in sorted(by_pref.items(), key=lambda kv: -len(kv[1]))[:6]:
        others = {r for q, rs in by_pref.items() if q != p for r in rs if r}
        hit = sum(1 for r in rests if any(r.startswith(o) for o in others))
        print(f"    {p:<12} {hit:>5} of {len(rests):<5} ({hit / max(1, len(rests)) * 100:.0f}%)")
    first_tok = Counter()
    two_tok = Counter()
    for k in keys:
        toks = camel_tokens(re.sub(r"^[a-z]+", "", k))
        if toks:
            first_tok[toks[0]] += 1
        if len(toks) > 1:
            two_tok[toks[0] + toks[1]] += 1
    print("  first word after prefix: " + ", ".join(f"{t} {n}" for t, n in first_tok.most_common(18)))
    print("  first two words:         " + ", ".join(f"{t} {n}" for t, n in two_tok.most_common(12)))

    # ---- 7. order + samples -----------------------------------------------------------------------------
    head("7. FILE ORDER (first 12 keys) AND 2 SAMPLE ENTRIES")
    print("  " + "  ".join(keys[:12]))
    sample_keys = []
    for k in keys:
        v = data[k]
        if isinstance(v, dict) and len(v) >= 2 and any(isinstance(s, dict) and s.get("parameterMap") for s in v.values()):
            sample_keys.append(k)
            break
    top_prefixes = [p for p, _ in pref.most_common(2)]
    for p in top_prefixes:
        k2 = next((k for k in keys if re.match(r"^[a-z]+", k) and re.match(r"^[a-z]+", k).group(0) == p
                   and k not in sample_keys), None)
        if k2 and len(sample_keys) < 2:
            sample_keys.append(k2)
    for k in sample_keys:
        print(f"\n  {k}: " + short(json.dumps(data[k], ensure_ascii=False), W * 2))
    print()

    # ---- full report --------------------------------------------------------------------------------------
    if full:
        with open("json_report.txt", "w", encoding="utf-8") as fh:
            fh.write("key\tprefix\tsection\tname\tparameterMap\n")
            for k in keys:
                v = data[k]
                m = re.match(r"^[a-z]+", k)
                if not isinstance(v, dict):
                    fh.write(f"{k}\t{m.group(0) if m else ''}\t(not an object)\t\t\n")
                    continue
                for t, s in v.items():
                    if isinstance(s, dict):
                        fh.write(f"{k}\t{m.group(0) if m else ''}\t{t}\t{s.get('name', '')}\t"
                                 f"{json.dumps(s.get('parameterMap') or {}, ensure_ascii=False)}\n")
        print("  wrote json_report.txt (one row per key per section, tab-separated; opens in Excel)\n")


if __name__ == "__main__":
    main()

import re

from .errors import GenError

METHODS = ("GET", "POST", "PUT", "PATCH", "DELETE")
BODY_METHODS = {"POST", "PUT", "PATCH"}
HTTP_DEFAULTS = {
    "GET": ("get", "fetch", "load", "find", "search", "check", "list", "read", "retrieve"),
    "POST": ("post", "create", "add", "submit", "send", "register", "login", "logout", "verify", "request", "save"),
    "PUT": ("put", "update", "edit", "replace"),
    "PATCH": ("patch",),
    "DELETE": ("delete", "remove", "clear", "cancel"),
}


def lower_first(s):
    return s[:1].lower() + s[1:]


def parse_use_case(raw):
    """'getTestingUseCase' -> ('getTesting', 'get')"""
    m = re.fullmatch(r"([A-Za-z][A-Za-z0-9]*?)UseCase", raw.strip())
    if not m:
        raise GenError(f"Use case name must look like getTestingUseCase (got '{raw}').")
    func = lower_first(m.group(1))
    return func, re.match(r"[a-z]+", func).group()


def infer_method(verb):
    for method, verbs in HTTP_DEFAULTS.items():
        if verb in verbs:
            return method
    return None


def parse_method(raw):
    m = raw.strip().lstrip("@").upper()
    if m not in METHODS:
        raise GenError(f"HTTP method must be one of {', '.join(METHODS)} (got '{raw}').")
    return m


def parse_endpoint(raw):
    """Accepts 'testing/v1', '/testing/v1' or '@GET("testing/v1")'. Returns (method|None, endpoint)."""
    s = raw.strip()
    m = re.fullmatch(r'@?(GET|POST|PUT|PATCH|DELETE)\s*\(\s*"(.*)"\s*\)', s, re.I)
    method = None
    if m:
        method, s = m.group(1).upper(), m.group(2)
    s = s.strip().strip('"').lstrip("/")
    if not s or any(c in s for c in ' "\\'):
        raise GenError(f"Invalid endpoint '{raw}'.")
    if "{" in s:
        raise GenError("Endpoints with path parameters ({id}) are not supported yet; "
                       "the generated function would be missing its @Path parameter.")
    return method, s


def parse_service_path(raw):
    """'example/sub/ExampleService.kt' -> ('example/sub', 'ExampleService')"""
    s = raw.strip().replace("\\", "/").strip("/")
    if s.endswith(".kt"):
        s = s[:-3]
    parts = s.split("/")
    stem, subdir = parts[-1], "/".join(parts[:-1])
    if ".." in parts or not re.fullmatch(r"[A-Z][A-Za-z0-9]*Service", stem):
        raise GenError(f"Service path must end with a PascalCase name ending in 'Service', "
                       f"e.g. example/sub/ExampleService.kt (got '{raw}').")
    return subdir, stem

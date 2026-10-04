"""Pipeline stages, in order. `preflight` always runs first; the rest can be picked with --only/--skip."""
from . import (data_di, datasource, datasource_impl, preflight, remote_di, repository, repository_impl,
               service, usecase, usecase_cluster)

STAGES = [
    ("service", service),
    ("datasource", datasource),
    ("datasource_impl", datasource_impl),
    ("remote_di", remote_di),
    ("repository", repository),
    ("repository_impl", repository_impl),
    ("data_di", data_di),
    ("usecase", usecase),
    ("usecase_cluster", usecase_cluster),
]
PREFLIGHT = preflight

# stage -> (config paths, config imports) it needs, checked before anything runs
REQUIRES = {
    "service": ([], ["BaseResponse"]),
    "datasource": ([], ["ResponseEntity"]),
    "datasource_impl": ([], []),
    "remote_di": (["remoteModule"], []),
    "repository": (["repository"], ["DataState"]),
    "repository_impl": (["repositoryImpl", "repository"], ["DataStateBoundResource"]),
    "data_di": (["dataModule", "repository", "repositoryImpl"], []),
    "usecase": (["useCase", "repository"], ["DataState"]),
    "usecase_cluster": (["useCase"], []),
}

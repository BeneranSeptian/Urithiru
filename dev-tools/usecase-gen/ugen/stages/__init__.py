"""Pipeline stages, in order. `preflight` always runs first; the rest can be picked with --only/--skip.

Planned next: repository, repository_impl, data_di, usecase, domain_di.
"""
from . import datasource, datasource_impl, preflight, remote_di, service

STAGES = [
    ("service", service),
    ("datasource", datasource),
    ("datasource_impl", datasource_impl),
    ("remote_di", remote_di),
]
PREFLIGHT = preflight

# model-gen

Part of dev-tools: see `../README.md` for setup and the full workflow. Run this before `gen-usecase`.

Generates Kotlin data classes from sample JSON. Types are inferred from the values.

    nano response.json                      # the payload your BaseResponse wraps
    nano request.json                       # only if the endpoint takes a request
    gen-model Example --dry-run             # preview
    gen-model Example                       # ResponseExample, ExampleEntity, Example
    gen-model Example --with-request        # + RequestExample, ExampleSpecEntity, ExampleSpec
    gen-model Example --with-request --no-from-mapper   # only RequestExample, plain data class

| Layer  | Response chain      | Request chain        |
|--------|---------------------|----------------------|
| remote | `ResponseExample`   | `RequestExample`     |
| entity | `ExampleEntity`     | `ExampleSpecEntity`  |
| model  | `Example`           | `ExampleSpec`        |

- Response: `ResponseX.toEntity()` -> `XEntity.toModel()` (ToEntityMapper / ToModelMapper).
- Request: `XSpec` -> `XSpecEntity.fromModel()` (FromModelMapper) -> `RequestX.fromEntity()` (FromEntityMapper).
- `--no-from-mapper`: for requests built from an existing entity; write the companion yourself.

**Reminder:** every run prints which JSON files it is using and when they were last edited. If a file is
still the shipped default sample it warns, and in a terminal it asks `Are these up to date? [y/N]`
(default No). `--yes` skips the question; `--dry-run` never asks.

Options: `--force` overwrite, `--yes`, `--project DIR`, `--config FILE`, `--response FILE_OR_JSON`, `--request FILE_OR_JSON`.

Inference: String, Boolean, Int (Long if > 2^31-1), Double (any decimal), nested object -> nested
class, array -> List<T> (elements merged). null / empty array / mixed types -> String + warning.

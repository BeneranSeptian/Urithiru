# model-gen

Generates the Response, Request (optional), Entity and Model Kotlin data classes
from sample JSON. Types are inferred from the values in the sample.

## Setup (once)

1. Edit `config.json`: set `projectRoot`, the four `paths` (relative to the project root),
   the mapper `imports`, and `serialization` (`gson`, `moshi`, `kotlinx` or `none`).
2. Put the tool on your PATH (see the parent folder's `bin/`).

## Usage

    nano response.json                  # paste the response payload (not the outer envelope)
    nano request.json                   # only if the endpoint takes a request
    gen-model Testing --dry-run         # preview
    gen-model Testing                   # response, entity, model
    gen-model Testing --with-request    # + request

Other options: `--force` (overwrite), `--project DIR` (another project),
`--response FILE_OR_JSON`, `--request FILE_OR_JSON`.
Existing files are never overwritten unless `--force` is given.

## Inference rules

String, Boolean, Int (Long if > 2^31-1), Double (any decimal in the sample),
nested object -> nested class, array -> List<T> (elements merged).
A null value, empty array or mixed types default to String with a warning.
Whole-number IDs come out as Int; change to Long by hand if needed.

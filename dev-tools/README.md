# dev-tools

```
dev-tools/
├── config.json        shared by every tool: projectRoot, paths, imports, DI snippets
├── bin/               put this on PATH; one symlink per command
├── model-gen/         gen-model     (own git repo)
└── usecase-gen/       gen-usecase   (own git repo)
```

## Setup (once)

```bash
cd ~/dev-tools
mkdir -p bin
ln -s ~/dev-tools/model-gen/gen-model     bin/gen-model
ln -s ~/dev-tools/usecase-gen/gen-usecase bin/gen-usecase
echo 'export PATH="$HOME/dev-tools/bin:$PATH"' >> ~/.zshrc && source ~/.zshrc
nano config.json        # projectRoot, paths (relative to projectRoot), import paths
```
Without PATH: `python3 ~/dev-tools/usecase-gen/gen-usecase ...`. Requires Python 3.8+, nothing else.

## Pipeline (run in this order, they are separate commands)

Step 0: edit `model-gen/response.json` (and `request.json` if there is a request).

1. `gen-model Example --with-request`        response/request data classes + mappers
2. `gen-usecase getExampleUseCase`           (does not create models, stops if missing) service -> datasource -> impl -> remote DI
3. (planned) repository, repository impl, data DI, use case, domain DI

Both tools skip files that exist and refuse to create a class/file name that is already
declared anywhere in the project. Use `--dry-run` first.

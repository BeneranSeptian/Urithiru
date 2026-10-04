# dev-tools

Two commands that remove the boilerplate of adding one API endpoint to the Android project:

| Command | What it generates |
|---|---|
| `gen-model`   | Response, Request, Entity and Model data classes (+ mappers) from sample JSON |
| `gen-usecase` | Service function, datasource, datasource impl, repository, repository impl, use case and all the DI lines |

They are separate commands. **Run `gen-model` first, then `gen-usecase`.** `gen-usecase` never creates
models: if they are missing it stops with "run gen-model first" and writes nothing.

```
dev-tools/
├── config.json       shared by both tools: projectRoot, paths, import paths, DI snippets
├── bin/              on your PATH, one symlink per command
├── model-gen/        gen-model     (own git repo)   response.json / request.json live here
└── usecase-gen/      gen-usecase   (own git repo)
```

Requirements: Python 3.8+ and nothing else (no pip packages, no internet, no Gradle).

---

## 1. Setup (once)

```bash
unzip ~/Downloads/dev-tools.zip -d ~          # creates ~/dev-tools
cd ~/dev-tools
mkdir -p bin
ln -s ~/dev-tools/model-gen/gen-model     bin/gen-model
ln -s ~/dev-tools/usecase-gen/gen-usecase bin/gen-usecase
echo 'export PATH="$HOME/dev-tools/bin:$PATH"' >> ~/.zshrc && source ~/.zshrc     # or ~/.bashrc
nano config.json
```

Without PATH you can run `python3 ~/dev-tools/usecase-gen/gen-usecase ...` instead.

Edit `config.json` (see the reference at the bottom): `projectRoot`, every `paths.*` (relative to
`projectRoot`, they must contain `/java/` or `/kotlin/` so the package can be derived) and every
`imports.*` (the real import path of `ToEntityMapper`, `BaseResponse`, `DataState`, ...).
It ships with `com.yourapp` placeholders.

Each tool can be its own git repo: `cd model-gen && git init && git add . && git commit -m init`.

## 2. Daily workflow

Example: a `POST testing/v1` endpoint whose model is called `Example`.

**Step 0 - put the real JSON in the sample files**

```bash
nano ~/dev-tools/model-gen/response.json     # the payload your BaseResponse wraps (not the envelope)
nano ~/dev-tools/model-gen/request.json      # only if the endpoint takes a body
```

**Step 1 - models**

```bash
gen-model Example --with-request --dry-run   # preview
gen-model Example --with-request             # drop --with-request for a GET
```
It prints which JSON files it used and when they were last edited, warns if one is still the default
sample, and asks `Are these up to date? [y/N]`.

**Step 2 - everything else**

```bash
gen-usecase postExampleUseCase
```
```
Service file (e.g. example/sub/ExampleService.kt): example/sub/ExampleService.kt
HTTP method [POST]:                                  <- Enter accepts the guess from the name
Endpoint (e.g. testing/v1): testing/v1
Model name (e.g. Example): Example
Request entity type [ExampleSpecEntity]:             <- POST/PUT/PATCH only, Enter accepts
Cluster module (e.g. featureAUseCaseModule, Enter to skip): featureAUseCaseModule
```
Then it shows every file it would create or change as a diff, plus warnings, and asks
`Apply these changes? [y/N]`. Nothing is written before you answer `y`.
Use `--dry-run` to only look, `--yes` to skip the question.

**Step 3 - finish by hand**

1. Move `PostExampleUseCase.kt` from `paths.useCase` to its real package (and fix the import
   the tool listed for the cluster file).
2. Add any imports listed under `Warnings` (existing files never get imports added, see rules).
3. `git add` the new files, build.

### What you get for `postExampleUseCase`, service `example/sub/ExampleService.kt`

```
gen-model                                          new files
  data/remote/response/ResponseExample.kt            ResponseExample -> ExampleEntity -> Example
  data/entity/ExampleEntity.kt
  domain/model/Example.kt
  data/remote/request/RequestExample.kt              ExampleSpec -> ExampleSpecEntity -> RequestExample
  data/entity/ExampleSpecEntity.kt
  domain/model/ExampleSpec.kt

gen-usecase                                        new file, or one function appended if it exists
  data/remote/service/example/sub/ExampleService.kt
  data/datasource/example/sub/ExampleRemoteDataSource.kt
  data/datasource/impl/example/sub/ExampleRemoteDataSourceImpl.kt
  domain/repository/example/sub/ExampleRepository.kt
  data/repository/example/sub/ExampleRepositoryImpl.kt
  domain/usecase/PostExampleUseCase.kt               new file only

                                                   lines added to existing files
  data/di/RemoteModule.kt     createService<ExampleService> + factory<ExampleRemoteDataSource>
  data/di/DataModule.kt       factory<ExampleRepository>
  <your cluster module>       factory { PostExampleUseCase(get()) }
```

Naming comes from the service file: `ExampleService` -> `ExampleRemoteDataSource` ->
`ExampleRemoteDataSourceImpl`, `ExampleRepository` -> `ExampleRepositoryImpl`. The sub-folder
(`example/sub/`) is reused under every path. A second use case in the same service
(`getExampleUseCase`, same service path) appends to those same files.

## 3. Rules both tools follow

- **Never overwrite** a file. A file or class that already exists is appended to (functions) or skipped.
- **No duplicates:** a function that already exists in the interface/class is skipped, a registration
  that already exists is skipped, and a new class/file whose name already exists anywhere in the project
  is refused (usually a typo in the path).
- **Existing files only get added lines.** The tool never reformats or edits your lines. The only
  exceptions: an empty one-line body like `interface X {}` is opened, and if an existing impl class does
  not inject the needed dependency, one constructor parameter is added (with a warning that its DI line
  may need another `get()`).
- **Imports only in newly created files.** Existing files (modules, existing interfaces, ...) are never
  given imports; anything truly missing is listed under `Warnings` with the exact `import` line.
- **Dry run first, all-or-nothing:** everything is computed in memory, you see the diff and confirm.
  Any error means nothing at all is written.
- **DI lines** (Koin by default) go below the last existing line of the same kind
  (`createService<`, `factory<...DataSource>`, `factory<...Repository>`, the last `factory` of the cluster).
  If there is none, they go at the end of the `module { }` block.

## 4. Not supported yet

Path parameters (`testing/{id}`), query parameters, list responses, cache injection in repositories,
creating a new use case cluster module (you name an existing one).

## 5. config.json reference

| Key | Meaning |
|---|---|
| `projectRoot` | Android project root (`~` ok). Override per run with `--project DIR` or `ANDROID_PROJECT_ROOT` |
| `serialization` | `gson`, `moshi`, `kotlinx` or `none` (annotation for fields whose JSON key differs from the Kotlin name) |
| `paths.response/request/entity/model` | where `gen-model` writes (flat) |
| `paths.service/dataSource/dataSourceImpl/repository/repositoryImpl` | where `gen-usecase` writes, plus the sub-folder from the service path |
| `paths.remoteModule` / `paths.dataModule` | folders containing `RemoteModule.kt` / `DataModule.kt` |
| `paths.useCase` | where use case files are created (no sub-folder) |
| `imports.*` | import paths of `ToEntityMapper`, `ToModelMapper`, `FromEntityMapper`, `FromModelMapper`, `emptyString`, `BaseResponse`, `ResponseEntity`, `toEntityWithData`, `DataState`, `DataStateBoundResource` |
| `naming.dataSourceSuffix` | default `RemoteDataSource` (`ExampleService` -> `ExampleRemoteDataSource`) |
| `di` | DI snippets and detection, see `usecase-gen/README.md` |

`gen-model` needs only the first four paths; `gen-usecase` only checks the keys of the stages you run.

## 6. Troubleshooting

| Message | Fix |
|---|---|
| `Android project root not found` | set `projectRoot` or pass `--project` |
| `class 'ResponseExample' not found ... Run gen-model first` | run `gen-model <Model>` (with `--with-request` for POST/PUT/PATCH) |
| `... would be created at X, but it already exists at Y` | the service path is mistyped; use the existing file's path |
| `cluster module 'x' not found` | the name must match `internal val x = module {`; press Enter to skip instead |
| `the closing brace shares a line with code` | an existing interface is written `{ fun a() }`; put `}` on its own line |
| `invalid JSON (line X, column Y)` | syntax error in `response.json` / `request.json` |
| `SKIP (exists)` / `already has a function` | nothing to do, that part was already generated |

Debug a single step: `gen-usecase ... --only repository_impl --dry-run -v` (see `usecase-gen/README.md`).

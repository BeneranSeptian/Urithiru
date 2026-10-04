# usecase-gen

Generates the whole data/domain chain for one use case. Run `gen-model` first.
The overall guide is in `../README.md`; this file is the reference for `gen-usecase`.

    gen-usecase getExampleUseCase                       # interactive
    gen-usecase postExampleUseCase --service example/sub/ExampleService.kt \
        --endpoint testing/v1 --model Example --cluster featureAUseCaseModule --dry-run

## Inputs

| Input | Flag | Notes |
|---|---|---|
| use case name | first argument | `getExampleUseCase` -> function `getExample`, class `GetExampleUseCase` |
| service file | `--service` | `example/sub/ExampleService.kt`, relative to `paths.service`; must end in `Service` |
| HTTP method | `--method` | default from the verb: get/fetch/load -> GET, post/create/submit -> POST, update -> PUT, patch -> PATCH, delete/remove -> DELETE |
| endpoint | `--endpoint` | `testing/v1` (leading `/` stripped, `@GET("testing/v1")` also accepted) |
| model name | `--model` | the name you gave `gen-model` |
| request entity | `--request-entity` | POST/PUT/PATCH only, default `<Model>SpecEntity` |
| request model | `--request-model` | normally derived: `<Model>Spec`, or read from `FromModelMapper<Model, Entity>` of a custom entity |
| cluster | `--cluster` | omit / Enter = inline element in `UseCaseModule.kt`; a name = existing `internal val x = module {`; `none` = only print the line |

Other flags: `--only a,b`, `--skip a,b`, `--stages`, `--dry-run`, `--yes`, `--verbose`, `--project`, `--config`.

POST, PUT and PATCH have a body: `@Body request: RequestExample` in the service, `entity: ExampleSpecEntity`
in the datasource, `request: ExampleSpec` in the repository and the use case.
GET and DELETE have none.

## Stages (for debugging)

    gen-usecase --stages                          list them
    gen-usecase ... --only repository_impl        run one stage (preflight always runs)
    gen-usecase ... --skip usecase_cluster
    gen-usecase ... --dry-run -v                  every decision + full diffs, writes nothing

| Stage | Does |
|---|---|
| preflight | validates input, finds the classes the selected stages use, says which files exist |
| service | `@METHOD("endpoint") suspend fun f(...): BaseResponse<ResponseX, XEntity>` |
| datasource | `suspend fun f(...): ResponseEntity<XEntity>` |
| datasource_impl | `override suspend fun f(...) = service.f(...).toEntityWithData { it?.toEntity() }` (`toEntityWithData` is a member of the response, nothing to import) |
| remote_di | `createService<XService>` and `factory<XRemoteDataSource>` in RemoteModule.kt |
| repository | `suspend fun f(...): DataState<X>` |
| repository_impl | `override suspend fun f(...) = DataStateBoundResource.createNetworkCall { ds.f(...) }.getResult()` |
| data_di | `factory<XRepository> { XRepositoryImpl(get()) }` in DataModule.kt |
| usecase | `class GetXUseCase(private val xRepository: XRepository) { suspend fun invoke() ... }` |
| usecase_cluster | `module { factory { GetXUseCase(get()) } },` as the first element of `createList(` in UseCaseModule.kt, or `factory { GetXUseCase(get()) }` in the cluster you name |

Generated code, POST example:

```kotlin
// repository impl
override suspend fun postExample(request: ExampleSpec) = DataStateBoundResource.createNetworkCall {
    exampleRemoteDataSource.postExample(ExampleSpecEntity.fromModel(request))
}.getResult()

// use case
class PostExampleUseCase(private val exampleRepository: ExampleRepository) {
    suspend fun invoke(request: ExampleSpec): DataState<Example> {
        return exampleRepository.postExample(request)
    }
}
```

## Rules

New file: written with all imports. Existing file: lines are only added; missing imports for what the new code
uses are added too (http annotations, response/request/entity/model classes, the injected type, DI classes),
unless already imported, covered by `pkg.*` or in the same package. Base classes from config.json
(`BaseResponse`, `ResponseEntity`, `DataState`, `DataStateBoundResource`) are only reported as warnings for
existing files. Existing functions/registrations are skipped. The use case file is never appended
to; if `GetXUseCase` already exists anywhere in the project it is skipped (you move the files by hand).
Between two multi-line members a blank line is added; one-line members stay compact. If a file already
has two or more functions, its own blank-line style is copied.

## Typical situations

- **Second use case in the same service:** same `--service` path. Functions are appended to the existing
  service, datasource, impl, repository and repository impl; DI lines are skipped as already registered.
- **A new service:** a new service path. New files are created and registered below the last existing line.
- **Only part of the chain:** `--only datasource,datasource_impl` etc.
- **Existing impl that does not inject the dependency:** one constructor parameter is added and a warning
  tells you to check its `get()` list in the DI module.

## DI (Koin by default)

Everything is under `di` in config.json. For each binding (`service`, `dataSource`, `repository`):

| Key | Meaning |
|---|---|
| `template` | the Kotlin to insert |
| `detect` | regexes; if one matches the file the binding is skipped |
| `after` | regex; the new line goes below the LAST line matching it (else at the end of the module) |
| `blank` | blank line between members when appended at the end |

`container` is the regex of the module declaration (default `\bmodule\b(?=\s*\{)`).
Placeholders: `{Service} {service} {DataSource} {DataSourceImpl} {Repository} {RepositoryImpl}`.
`createService` must already be imported in RemoteModule.kt.

### Use case registration

- **Default (no cluster):** `UseCaseModule.kt` in `paths.useCaseModule` must contain a `createList(` call whose
  elements start on the line after it. Each use case adds one inline element as the FIRST element
  (newest on top, and no existing line needs a new comma):

```kotlin
val usecaseModule = createList(
    module { factory { PostExampleUseCase(get()) } },
    module { factory { GetExampleUseCase(get()) } },
    featureAUseCaseModule,
    featureBUseCaseModule
)
```

- **Named cluster:** `--cluster featureAUseCaseModule` adds `factory { GetXUseCase(get()) }` below the last
  `factory`/`single`/`viewModel` of `internal val featureAUseCaseModule = module { ... }`.
- **`--cluster none`:** nothing is written, the line and import are printed.
- A use case that is already registered anywhere (`GetXUseCase(get(` or `::GetXUseCase`) is skipped.

Hilt example (replace the `di` block):

```json
"di": {
  "container": "\\b(?:object|class)\\s+\\w*Module\\b",
  "service": {
    "template": "@Provides\n@Singleton\nfun provide{Service}(retrofit: Retrofit): {Service} =\n    retrofit.create({Service}::class.java)",
    "detect": ["\\bfun\\s+provide{Service}\\b"], "blank": true },
  "dataSource": {
    "template": "@Provides\n@Singleton\nfun provide{DataSource}({service}: {Service}): {DataSource} =\n    {DataSourceImpl}({service})",
    "detect": ["\\bfun\\s+provide{DataSource}\\b"], "blank": true },
  "repository": {
    "template": "@Provides\n@Singleton\nfun provide{Repository}(ds: {DataSource}): {Repository} =\n    {RepositoryImpl}(ds)",
    "detect": ["\\bfun\\s+provide{Repository}\\b"], "blank": true }
}
```

## Tests

    python3 -m unittest discover -s tests -v

37 tests: Kotlin text helpers, plus end-to-end runs on a scratch project that check the generated code,
that existing files only ever gain lines, imports (new vs existing files), duplicates, inline and named
use case registration, DI variants and errors.

# usecase-gen

    gen-usecase getExampleUseCase
    # asks: service file, HTTP method (default from the name), endpoint, model name,
    #       request entity (POST/PUT/PATCH only, default <Model>SpecEntity)

    gen-usecase postExampleUseCase --service example/sub/ExampleService.kt \
        --endpoint testing/v1 --model Example --dry-run

Naming: `getExampleUseCase` -> function `getExample`. `ExampleService` -> `ExampleRemoteDataSource`
-> `ExampleRemoteDataSourceImpl` (property `exampleService`). The same sub-folder (`example/sub/`)
is used under paths.service, paths.dataSource and paths.dataSourceImpl.

HTTP: POST/PUT/PATCH imply `@Body request: RequestX`; GET/DELETE have no body. Default method comes
from the verb (get/fetch/load -> GET, post/create/submit -> POST, update -> PUT, delete/remove -> DELETE).
Path parameters (`{id}`) and query parameters are not supported yet.

## Stages (debugging)

    gen-usecase --stages                       list
    gen-usecase ... --only service             run one stage (preflight always runs)
    gen-usecase ... --skip remote_di
    gen-usecase ... --dry-run -v               every decision + full diffs, writes nothing

| stage           | does |
|-----------------|------|
| preflight       | validates input, finds Response/Entity/Request/Spec classes, reports which files exist |
| service         | add `@METHOD` function to the service interface (new or existing) |
| datasource      | add function to the datasource interface |
| datasource_impl | add override; adds the service to the constructor if missing |
| remote_di       | add service + datasource bindings to RemoteModule.kt |

All stages work in memory; files are written together at the end, after the diff and a y/N
confirmation (`--yes` skips it). Any error means nothing is written.

## Duplicate protection

Never overwrites a file. Skips (with a message) a function that already exists in the target
interface/class. Refuses to create an interface/class whose name or file name already exists
elsewhere in the project (usually a typo in the service path). Skips DI bindings that already exist.

## DI (Koin by default)

`remote_di` edits `paths.remoteModule/RemoteModule.kt`. For each binding it:

1. skips it if it is already registered (`detect` regexes),
2. else inserts it ABOVE a marker comment (`// new service will go here`,
   `// new datasource will go here`; spacing/case don't matter),
3. else inserts it after the LAST existing matching line (`createService<...>` / `factory<...DataSource>`),
4. else appends it to the end of the `module { }` block.

Generated lines:

    factory { createService<ExampleService>(get()) }
    factory<ExampleRemoteDataSource> { ExampleRemoteDataSourceImpl(get()) }

The impl gets only the service (`get()`), matching the constructor the generator creates.
Everything is under `di` in config.json (`container`, `imports`, and per binding `template`, `detect`,
`marker`, `after`, `blank`). Placeholders: `{Service} {service} {DataSource} {DataSourceImpl}`.
Your own `createService` helper must already be imported in RemoteModule.kt (add it to `di.imports` if not).

Hilt example (replace the `di` block):

    "di": {
      "container": "\\b(?:object|class)\\s+\\w*Module\\b",
      "imports": ["dagger.Provides", "javax.inject.Singleton", "retrofit2.Retrofit"],
      "service": {
        "template": "@Provides\n@Singleton\nfun provide{Service}(retrofit: Retrofit): {Service} =\n    retrofit.create({Service}::class.java)",
        "detect": ["\\bfun\\s+provide{Service}\\b"], "blank": true },
      "dataSource": {
        "template": "@Provides\n@Singleton\nfun provide{DataSource}({service}: {Service}): {DataSource} =\n    {DataSourceImpl}({service})",
        "detect": ["\\bfun\\s+provide{DataSource}\\b"], "blank": true }
    }

## Naming

Datasource name = service name with `Service` replaced by `naming.dataSourceSuffix`
(default `RemoteDataSource`): `ExampleService` -> `ExampleRemoteDataSource` -> `ExampleRemoteDataSourceImpl`.
Set it to `DataSource` in config.json to get `ExampleDataSource` instead.

## Tests

    python3 -m unittest discover -s tests -v

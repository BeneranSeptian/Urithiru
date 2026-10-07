# CLAUDE.md

Guidance for Claude Code when working in this repository. For the full project overview, read `SUMMARY.md`. For the roadmap and ideas, read `FOR READING.md`.

## Project

Urithiru is a modular Android app template (Clean Architecture, Jetpack Compose, Hilt, Retrofit, Coroutines/Flow, and KSP-generated navigation). The sample data is Elden Ring (weapons and bosses) from `eldenring.fanapis.com`, plus onboarding content from JSONBin and a Twitch OAuth flow.

## Commands

```bash
./gradlew assembleDevDebug                 # build the default (dev) flavor
./gradlew :app:installDevDebug             # install on a connected device/emulator
./gradlew :api-a:compileDevDebugKotlin     # fast compile check for one module
./gradlew clean assembleDevDebug           # use after changing @FeatureRoute annotations if KSP output looks stale
```

- Flavors: `dev` (default), `uat`, `beta`, `prod`. Base URLs come from `productFlavorProperties/<flavor>.properties` and become `BuildConfig` fields.
- Endpoint paths come from `<api-module>/microservice.properties` and also become `BuildConfig` fields (for example `WEAPONS_V1`).
- Library-module test stubs don't compile yet, because the convention plugins don't add JUnit or AndroidX test dependencies. Only `:app` has test dependencies.

## Module rules

- **Package root is always `com.septianbeneran.urithiru`.** A module's namespace is `com.septianbeneran.urithiru.<module-name with - replaced by .>`. For example, `api-twitch` uses `com.septianbeneran.urithiru.api.twitch`. Source folders must match the package.
- Dependency direction: `app → feature-* → api-* → core, core-entity`. Features may also use `core-ui` and `core-navigation`.
- **Feature modules never depend on each other.** They navigate through shared `@Serializable` route classes in `core-navigation`.
- Use `moduleImplementation(projects.xxx)` (from `buildlogic/GradleExtension.kt`) for every inter-module dependency, e.g. `moduleImplementation(projects.coreUi)`. `projects.*` are Gradle's type-safe project accessors, generated from the `include(...)` list in `settings.gradle.kts`, so a misspelled or missing module fails to compile. Never use `project(":x")` in module build files. (The only exception is inside `buildlogic`, which is an included build and can't see the accessors.)
- New modules apply one convention plugin:
  - `api-convention` for data/API modules
  - `feature-convention` for `feature-*` modules (Compose + Hilt-navigation, Coil and the navigation processor)
  - `compose-convention` for shared Compose libraries without screens (`core-ui`, `core-navigation`)
  - `base-convention` for plain libraries

## Layering per API module (`api-*`)

```
data/remote/api/        Retrofit interface; use @GET/@POST with @Url, with the path from BuildConfig
data/remote/dto/        Response DTOs + mapToEntity() functions → entities live in :core-entity
data/remote/service/    XRemoteDataSource + Impl, extending BaseDataSource; wrap calls in getResult { }
data/local/             XCache + Impl, wrapping BaseDataStore
repository/             XRepository + Impl, extending BaseRepository; use resultFlow(...).mapToEntity(...)
domain/get|load|post/   One use case per action: interface + Impl, with operator fun invoke
di/                     Hilt modules in SingletonComponent: @Binds Impl → interface; @Provides only for the Retrofit Api
```

Every `Impl` has an `@Inject constructor` and is wired with `@Binds`, never built by hand in `@Provides`. Data sources, caches and repositories are `@Singleton`; use cases are unscoped.

Network calls return `Flow<ApiResult<T>>` (`Loading` → `Success`/`Error`). UI state uses `BaseState`.

JSON is kotlinx-serialization only, through the shared `Json` from `core/di/SerializationModule`. Never add Gson. Mark DTOs, and any entity saved with `BaseDataStore.saveObject`, as `@Serializable`, and rename fields with `@SerialName`.

## Presentation conventions (feature modules)

- Each screen has `screen/stateaction/XStateAction.kt` with:
  - `XUiState`: a data class
  - `XAction`: a sealed interface of user intents
  - `XNonce : BaseNonce`: one-shot events, mostly navigation
- ViewModels are `@HiltViewModel` and extend `BaseViewModel`. They expose `uiState: StateFlow` and a single `onAction(action)`. Use `collectApi(...)` for network flows, `collectLocalData(...)` for cache flows, and `sendNonce(...)` for events.
- Screens wrap their content in `BaseScreen(viewModel = …)` and observe events with `NonceObserver(viewModel.nonce) { … }`.
- Reuse `core-ui` components (`UriButton`, `UriDialog`, `UriToast`, `UriTopAppBar`, …) and `UrithiruTheme` tokens before writing new UI. New shared components go in `core-ui/component`, use the `Uri` prefix, and need a `@Preview`.

## Navigation (KSP: `:navigation-processor`)

1. Add a `@Serializable` route in `core-navigation/routeparams/<feature_x>/`. For a new feature, also add a graph object in `core-navigation/graph/`.
2. Annotate the screen composable `@FeatureRoute(routeParams = XRoute::class)`. The composable must take `(navigator: Navigator)`. Exactly one screen per feature module also sets `graph = FeatureXNavGraph::class`; that screen is the start destination.
3. Build. KSP generates the feature graph and `registerAllFeatureGraphs()`, which `NavHostChamber` in `:app` calls. Never register screens by hand.

The parameter is `graph`, not `navGraph` (the README is out of date on this).

## Planned design decisions

- **Bottom bar / navigation rail:** use `NavigationSuiteScaffold`, placed in `:app` around the single `NavHost` in `NavHostChamber`.
  - Top-level tabs are a hand-written `TopLevelDestination` list in `:app` that points at feature graph objects.
  - The bar hides automatically outside tab graphs (splash and onboarding).
  - Tab switching uses a `Navigator.navigateToTopLevel(graph)` helper with `saveState` and `restoreState`.
  - Features never know about the bar.
  - A feature with its own internal sections uses top tabs inside the feature, never a second bottom bar.
  - `BaseScreen` applies `systemBarsPadding()`, so it needs inset handling when it's inside the scaffold.

## Working style

- The owner reviews changes on their phone and on a laptop without AI tools. Keep commits small, with clear messages, and keep docs (`SUMMARY.md`, `FOR READING.md`) up to date when the architecture changes.
- Don't commit secrets. API keys and client secrets belong in `local.properties` (git-ignored) and get injected via `BuildConfig`.

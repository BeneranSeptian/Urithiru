# CLAUDE.md

Guidance for Claude Code when working in this repository. For the full project overview, read `SUMMARY.md`. For the roadmap and ideas, read `FOR READING.md`.

## Project

Urithiru is a modular Android app template (Clean Architecture, Jetpack Compose, Hilt, Retrofit, Coroutines/Flow, and KSP-generated navigation). The sample data is Elden Ring (weapons and bosses) from `eldenring.fanapis.com`, onboarding text from JSONBin, login from DummyJSON (`api-dummyjson`, `feature-auth`), and a Twitch OAuth flow. It is becoming "Urithiru Showcase", a working template app with 4 tabs (Catalog, Shop, Explore, Profile); see `FOR READING.md`.

## Commands

```bash
./gradlew assembleDevDebug                 # build the default (dev) flavor
./gradlew :app:installDevDebug             # install on a connected device/emulator
./gradlew :api-eldenring:compileDevDebugKotlin     # fast compile check for one module
./gradlew clean assembleDevDebug           # use after changing @FeatureRoute annotations if KSP output looks stale
```

- Flavors: `dev` (default), `uat`, `beta`, `prod`. Base URLs come from `productFlavorProperties/<flavor>.properties` and become `BuildConfig` fields.
- Endpoint paths come from `<api-module>/microservice.properties` and also become `BuildConfig` fields (for example `WEAPONS_V1`).
- Every module gets JUnit and AndroidX test through `testDependencies()` (`buildlogic/DependencyExtension.kt`). Run unit tests with `./gradlew testDevDebugUnitTest`. Compose UI test dependencies are only in `:app`.

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

Network calls return `Flow<ApiResult<T>>` (`Loading` → `Success`/`Error`). Errors are a typed `AppError` (`:core`), mapped once in `BaseDataSource.getResult`; never invent error codes. UI state uses `BaseState`. Use theme colors (`MaterialTheme.colorScheme`), never `Neutral.*`/`Highlight.*` directly, for text and surfaces in components, so they work in dark mode. Show failures with `UriErrorState` (message from `AppError.toMessage()`) and empty results with `UriEmptyState`. Pass a `key` to `collectApi` when a newer call should cancel an older one (search, refresh).

JSON is kotlinx-serialization only, through the shared `Json` from `core/di/SerializationModule`. Never add Gson. Mark DTOs, and any entity saved with `BaseDataStore.saveObject`, as `@Serializable`, and rename fields with `@SerialName`.

## Presentation conventions (feature modules)

- Each screen has `screen/stateaction/XStateAction.kt` with:
  - `XUiState`: a data class
  - `XAction`: a sealed interface of user intents
  - `XNonce : BaseNonce`: one-shot events, mostly navigation
- ViewModels are `@HiltViewModel` and extend `BaseViewModel`. They expose `uiState: StateFlow` and a single `onAction(action)`. Use `collectApi(...)` for network flows, `collectLocalData(...)` for cache flows, and `sendNonce(...)` for events.
- Screens wrap their content in `BaseScreen(viewModel = …)` and observe events with `NonceObserver(viewModel.nonce) { … }`.
- Reuse `core-ui` components (`UriButton`, `UriDialog`, `UriToast`, `UriTopAppBar`, `UriErrorState`, `UriEmptyState`, `UriSearchBar`, `UriTextField`, `UriImage`, `UriListFooter`, …) and `UrithiruTheme` tokens before writing new UI. New shared components go in `core-ui/component`, use the `Uri` prefix, and need a `@Preview`.
- Colors come from `MaterialTheme.colorScheme`; every Material 3 role is mapped to the Urithiru palette in `UrithiruTheme.kt` for light and dark. When a Material component looks purple, a role is missing there; fix it in the theme, not in the screen.
- Light/dark is a user setting: `ThemeMode` (`:core` `settings/`) saved by `AppSettings`. `UrithiruProject.defaultThemeMode` applies until the user picks one. Save a new mode with `AppSettings.saveThemeMode`; `MainActivity` recreates itself and applies it to the system bars, the activity's resources and, on Android 12+, the launch window. Don't read `isSystemInDarkTheme()` directly in screens; the theme already reflects the chosen mode.

## Navigation (KSP: `:navigation-processor`)

1. Add a `@Serializable` route in `core-navigation/routeparams/<feature_x>/`. For a new feature, also add a graph object in `core-navigation/graph/`.
2. Annotate the screen composable `@FeatureRoute(routeParams = XRoute::class)`. The composable must take `(navigator: Navigator)`. Exactly one screen per feature module also sets `graph = FeatureXNavGraph::class`; that screen is the start destination.
3. Build. KSP generates the feature graph and `registerAllFeatureGraphs()`, which `NavHostChamber` in `:app` calls. Never register screens by hand.

The parameter is `graph`, not `navGraph`.

## Top-level tabs (bottom bar / navigation rail)

- `NavigationSuiteScaffold` in `:app` wraps the single `NavHost` in `NavHostChamber`: a bottom bar on phones, a rail on larger screens.
  - The tabs are the hand-written `TopLevelDestination` enum in `:app` (Catalog, Shop, Explore, Profile), each pointing at a feature graph object. The first entry is home. A new tab is one enum entry plus a feature module whose start screen sets that `graph`.
  - The bar shows only on a tab's root screen (the start destination of its graph). Detail screens, splash, onboarding and login (`FeatureAuthNavGraph`) are full screen.
  - Switch tabs with `Navigator.navigateToTopLevel(graph)`. It pops up to the home graph's start destination (not the graph itself, which would pop home away) with `saveState`/`restoreState`, so each tab keeps its own history and Back returns to the first tab.
  - Leave splash, onboarding or login with `Navigator.navigateToHome()`, which clears the back stack. Features never reference the home tab directly.
  - Features never know about the bar. `NavHostChamber` consumes the navigation bar inset while the bar shows, so `BaseScreen`'s `systemBarsPadding()` doesn't pad twice.
  - A feature with its own internal sections uses top tabs inside the feature, never a second bottom bar.
- Icons are official Material Symbols (`core-ui/res/drawable`, `name.xml` outlined and `name_filled.xml` filled), never hand-drawn.

## Working style

- The owner reviews changes on their phone and on a laptop without AI tools. Keep commits small, with clear messages, and keep docs (`SUMMARY.md`, `FOR READING.md`) up to date when the architecture changes.
- Don't commit secrets. API keys and client secrets belong in `local.properties` (git-ignored) and get injected via `BuildConfig`.

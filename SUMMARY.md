# Urithiru Project — Summary

A modular Android app template built with Clean Architecture, Jetpack Compose, Hilt, Retrofit and Coroutines/Flow. The sample domain is **Elden Ring** data (weapons, bosses) from `eldenring.fanapis.com`, plus onboarding content from **JSONBin** and an OAuth token flow for **Twitch/IGDB**.

- Package root: `com.septianbeneran.urithiru` for every module. A module's namespace is the root plus its module name, with `-` replaced by `.`
- SDK: compileSdk 37, minSdk 24, targetSdk 35, Java 21
- Build: Gradle 9.x, AGP 9.4.1, Kotlin 2.4.20, KSP 2.3.7, Compose BOM 2026.09.00, Hilt 2.60.1

---

## Module Map

| Module | Kind | Responsibility |
|---|---|---|
| `:app` | application | `UrithiruProject` (`@HiltAndroidApp`), `MainActivity`, and `NavHostChamber` (the root `NavHost`, which starts at `FeatureSplashNavGraph` and calls the generated `registerAllFeatureGraphs`) |
| `:buildlogic` | included build | Convention plugins, `AppConfig`, `ProductFlavor`, dependency bundles |
| `:core` | library | Base data/repository classes, `ApiResult`/`BaseState`, Retrofit and OkHttp DI, DataStore wrapper, dispatchers, permission handling |
| `:core-entity` | library | Domain models: `Weapon`, `Attributes`, `Scaling`, `Boss`, `OnBoardingPageData`, `TwitchOAuthToken` |
| `:core-navigation` | compose lib | `@FeatureRoute` and `@FeatureGraph` annotations, `@Serializable` graph objects and route params, the `Navigator` wrapper |
| `:core-ui` | compose lib | `BaseScreen`, `BaseViewModel`, `NonceObserver`, theme (`UrithiruTheme`, colors, typography), and the `Uri*` design-system components |
| `:api-a` | api lib | Elden Ring **weapons** (list with paging/search, detail, and a DataStore cache) |
| `:api-b` | api lib | **JSONBin** onboarding pages (with a cache) |
| `:api-twitch` | api lib | Twitch OAuth `client_credentials` token and `RefreshTokenImpl` |
| `:feature-splash` | compose lib | Splash screen. It fetches onboarding data, then navigates to onboarding |
| `:feature-b` | compose lib | OnBoarding → PersonalizeExperience → Home/Landing, plus a BossList screen |
| `:feature-a` | compose lib | Weapon list (search, infinite scroll) and weapon detail |
| `:navigation-processor` | JVM (KSP) | Generates route and graph builders from annotations |

### Dependency direction
```
app ──► feature-* ──► api-* ──► core, core-entity
          │
          └──► core-ui ──► core-navigation ──► core
```
Feature modules never depend on each other. They navigate using shared route classes from `core-navigation`.

---

## Build System (`buildlogic/`)

Convention plugins (`buildlogic/src/main/kotlin`):
- **`base-convention`**: Android library, Hilt and KSP, namespace `com.septianbeneran.urithiru.<module.name>`, flavors, Java 21, the kotlinx-serialization compiler plugin, plus `baseDependencies()` (Hilt, kotlinx-serialization-json, DataStore).
- **`api-convention`**: base convention plus Retrofit and kotlinx-serialization-json. It also loads the module's `microservice.properties` into `BuildConfig` (for example `WEAPONS_V1=weapons/` and `BOSSES_V1=bosses/`).
- **`compose-convention`**: base convention plus Compose, Navigation, Coil, Hilt-navigation, and `ksp(project(":navigation-processor"))`.
- **`app-convention`**: application setup with the same flavor logic plus `applicationIdSuffix`.

**Product flavors** (dimension `environment`): `dev` (default, `.dev`), `uat` (`.uat`), `beta` (`.beta`), `prod`. Each flavor reads `productFlavorProperties/<flavor>.properties` and turns every key into a `BuildConfig` String field. The keys are `APP_NAME`, `ELDEN_RING_BASE_URL`, `TWITCH_BASE_URL`, `IGDB_BASE_URL` and `JSON_BIN_BASE_URL`.

`moduleImplementation(projects.xxx)` (`GradleExtension.kt`) takes a type-safe project accessor. These are enabled by `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in `settings.gradle.kts` and generated from its `include(...)` list, so only modules that exist can be referenced, and the IDE autocompletes them. Adding a module to `settings.gradle.kts` creates its accessor (`api-twitch` → `projects.apiTwitch`). The root project is named `Urithiru`, because accessors require a name without spaces. If the root `extra` defines a version for that module name, it resolves to the Maven artifact `com.septianbeneran.urithiru:name:version` instead, which lets a module be swapped for a published version.

---

## Architecture & Data Flow

```
Screen ─► ViewModel ─► UseCase ─► Repository ─► RemoteDataSource ─► Retrofit Api
                                      └──► Cache (BaseDataStore / kotlinx JSON)
```

**Core building blocks (`:core`)**
- `BaseDataSource.getResult { api.call() }` turns a Retrofit `Response<T>` into `ApiResult.Success` or `ApiResult.Error`. Exceptions map to error code `6969`.
- `resultFlow(networkCall, dispatcher)` emits `Loading` and then the result, running on `io()`.
- `BaseRepository.mapToEntity(transform, saveResult)` maps the DTO to an entity. If `saveResult` is given, it also saves the result to the cache on the `@ApplicationScope` coroutine scope.
- `ApiDto<T>(success, count, data)` is the Elden Ring envelope. `JsonBinDto` is the JSONBin envelope.
- `BaseState`: `StateInitial | StateLoading | StateSuccess | StateFailed` (UI-side state).
- `NetworkModule` provides one `Retrofit` per backend, using the qualifiers `@EldenRingNetwork`, `@TwitchNetwork`, `@IgdbNetwork` and `@JsonBinNetwork`. Every `Retrofit` uses the kotlinx-serialization converter.
- `SerializationModule` provides the single shared `Json` instance (`ignoreUnknownKeys = true`, `explicitNulls = false`), used by both Retrofit and `BaseDataStore`. Gson is not used anywhere.
- JSON models must be `@Serializable`: DTOs, the `ApiDto`/`JsonBinDto` envelopes, and any entity stored with `saveObject`. Rename fields with `@SerialName`, not `@SerializedName`.
- `BaseDataStore` is a Preferences DataStore with primitive save/read and `saveObject`/`readObject` through kotlinx-serialization (the shared `Json`).

**Per-API module layout** (for example `api-a`):
```
data/remote/api/         Retrofit interface (@GET with @Url, so the endpoint path comes from BuildConfig)
data/remote/dto/         Response DTOs with mapToEntity functions (for example mapToWeapon)
data/remote/service/     XRemoteDataSource + Impl (extends BaseDataSource)
data/local/              XCache + Impl (wraps BaseDataStore)
repository/              XRepository + Impl (extends BaseRepository, exposes `cache`)
domain/get/              Get*UseCase: network-backed Flow<ApiResult<Entity>>
domain/load/             Load*UseCase: cache-backed Flow<Entity>
di/                      Hilt modules: @Binds for Impl → interface, @Provides only for the Retrofit Api
```

**Presentation pattern (`:core-ui` + features)**
- Each screen has a `stateaction/` file containing `XUiState` (data class), `XAction` (sealed interface, an MVI-style intent), and `XNonce : BaseNonce` (one-shot events such as navigation).
- The ViewModel extends `BaseViewModel` and exposes `uiState: StateFlow` and `onAction(action)`. It uses:
  - `collectApi(flow, isCentralLoading, onLoading, onError, onSuccess)`, which also toggles the global loading dialog
  - `collectLocalData(flow) { … }` for cache flows
  - `sendNonce(nonce)`, which sends to a `Channel` that is collected by `NonceObserver` in the composable
- `BaseScreen(viewModel, topAppBarArgs, onBack, …) { properties -> }` handles the central loading dialog, the back handler, system bar padding, the optional `UriTopAppBar`, and runtime permissions (`properties.handlePermission`).

---

## Navigation Code Generation (KSP)

1. Declare a `@Serializable` graph object (`core-navigation/.../graph`) and a route (`.../routeparams/<feature>`). Use an `object`, or a `data class` when the route takes arguments.
2. Annotate the screen composable:
   ```kotlin
   @FeatureRoute(routeParams = WeaponListRoute::class, graph = FeatureANavGraph::class) // set graph only on the start destination
   @Composable fun WeaponListRoute(navigator: Navigator) { … }
   ```
3. **`FeatureProcessor`** (runs in feature modules):
   - generates one `NavGraphBuilder` extension per route
   - generates `fun NavGraphBuilder.featureANavGraph(navigator)` annotated `@FeatureGraph`, in package `com.septianbeneran.urithiru.feature.graph`
   - fails the build if a module has zero or more than one start destination
4. **`GraphProcessor`** (runs in the app, enabled by `ksp { arg("isAppModule","true") }`) scans the `...feature.graph` package. It generates `registerAllFeatureGraphs(navigator)` in `com.septianbeneran.urithiru.graph`.
5. `NavHostChamber` calls `registerAllFeatureGraphs`, so new screens need no manual registration.

**Current routes / app flow**
```
Splash (FeatureSplashNavGraph start)
  └─► OnBoarding ─► PersonalizeExperience ─► Landing/Home (FeatureBNavGraph start)
                                                └─► WeaponList (FeatureANavGraph start) ─► WeaponDetail(id)
```
Each step pops the previous screen with `popUpTo(..., inclusive = true)`.

---

## Design System (`core-ui/component`)
`UriAvatar`, `UriBadge`, `UriBanner`, `UriButton`, `UriCheckBox`, `UriDialog`, `UriDivider`, `UriDot`, `UriListItem`, `UriLoader`, `UriMediaPlaceHolder`, `UriProgressBar`, `UriRadioButton`, `UriStepper`, `UriToast`, `UriTopAppBar`, `CentralLoadingDialog`. The theme lives in `core-ui/theme` (with custom fonts under `res/font`).

---

## How-To Cheatsheet

**Add an API endpoint:** add a DTO, then the Retrofit method (`@Url` path). Then add the DataSource method (`getResult { … }`), the Repository method (`resultFlow(...).mapToEntity(...)`), and a UseCase interface with its Impl. Wire them in the module's `di/` and add any new path constant to `microservice.properties`.

**Add a new backend:** add `*_BASE_URL` to every `productFlavorProperties/*.properties`, add a qualifier annotation in `core/annotation`, and add a `Retrofit` provider in `NetworkModule`. Then create a new `api-*` module using `api-convention`, and register it in `settings.gradle.kts` and `app/build.gradle.kts`.

**Add a screen:** add the route (plus a graph object if this is a new feature) in `core-navigation`, add the `@FeatureRoute` composable, ViewModel and stateaction in the feature module, then build.

**Add a feature module:** use `compose-convention` and depend on `core`, `core-entity`, `core-navigation`, `core-ui` and the needed `api-*`. Add it to `settings.gradle.kts` and to the `app` dependencies.

---

## Observations / Known Issues (as of 2026-10-06)

> On the `improvement` branch, the broken `WeaponRepository` import is fixed and every package is now under `com.septianbeneran.urithiru`. See also `CLAUDE.md` (conventions) and `FOR READING.md` (roadmap and ideas).

- The README's "New Screen Route" section uses `navGraph =` in `@FeatureRoute`, but the actual parameter is **`graph`**. The README module list also leaves out `:api-twitch` and `:navigation-processor`, and it says Gradle 9.3.1 while the libraries were updated to 9.4.1.
- `@IgdbNetwork` Retrofit and `RefreshTokenInterface`/`RefreshTokenImpl` are provided, but nothing uses them yet. No IGDB API module exists so far.
- The splash screen hardcodes the JSONBin `binId` in `SplashScreenViewModel`.
- Paging assumes a page size of 20 (`isEndReached = newWeapons.size < 20`) in `WeaponListViewModel`.
- There is a leftover debug `println("masuk observer")` in `PersonalizeExperienceScreen.kt`.
- `RefreshTokenInterface.postRereshToken` has a typo ("Reresh").
- Tests are only the default `ExampleUnitTest` and `ExampleInstrumentedTest` stubs. In library modules those stubs don't even compile, because the convention plugins add no JUnit or AndroidX test dependencies (only `:app` has them).

---

## Suggested Improvements

> **Status:** items that are ~~crossed out~~ and marked ✅ are done. Everything else is still open.

### Correctness & robustness
- **Cancel in-flight requests in `BaseViewModel.collectApi`.** Each call starts a new `viewModelScope.launch` and keeps no handle to it. When a user taps search several times quickly, those requests run at the same time and can append results out of order. Return the `Job` and cancel the previous one for the same key, or switch to a `MutableStateFlow` of queries combined with `flatMapLatest`.
- **Remove the `TODO()` calls in `TwitchRepositoryImpl`** (`saveTwitchToken` and `loadTwitchToken`). These functions crash at runtime if anything calls them. Either implement them using `TwitchCache` or take them out of the interface.
- ~~**Pick one JSON library.** Retrofit uses `GsonConverterFactory`, but the DTOs carry kotlinx `@Serializable` and some also carry Gson `@SerializedName`. The kotlinx converter is a dependency but is never used. Gson ignores Kotlin nullability and default values, so a missing field can silently become `null` inside a non-null type. Standardizing on kotlinx-serialization (for both Retrofit and `BaseDataStore`) would avoid that.~~
  ✅ **Done (2026-10-07).** Retrofit and `BaseDataStore` both use kotlinx-serialization through one shared `Json`. Gson and the unused JakeWharton converter are removed. `base-convention` now applies the serialization compiler plugin; before this, `@Serializable` in `api-*` and `core-entity` generated nothing.
- **Replace the magic error code `6969`.** Use typed errors instead, for example `sealed class AppError { Network, Http(code), Parse, Unknown }`, so the UI can show meaningful messages and offer a retry only when it makes sense.
- **Get the page size from one place.** `isEndReached = size < 20` assumes the API returns 20 items, but no `limit` is sent with the request. Send `limit` explicitly and compare against the same constant.
- **Keep secrets out of source.** Twitch `client_id`/`client_secret` should come from `local.properties` or CI secrets and be injected into `BuildConfig`, not hardcoded. The JSONBin `binId` should move to `microservice.properties`.

### Security & release readiness
- **Limit `LogcatInterceptor` to debug builds.** Today it logs every header, including `Authorization`, and full bodies in all flavors, `prod` included. Add it only when `BuildConfig.DEBUG` is true, or redact sensitive headers.
- **Add a `buildTypes { release { isMinifyEnabled = true; isShrinkResources = true } }` block** with R8 keep rules for Retrofit and kotlinx-serialization models. No minify or shrink setup exists yet.
- **Set `targetSdk` closer to `compileSdk`.** `targetSdk` is 35 while `compileSdk` is 37, and Play's target API requirements will catch up.

### Architecture & DI
- ~~**Make the DI style consistent.** Classes have `@Inject constructor` *and* are also built by hand in `@Provides` methods. Use `@Binds` abstract modules instead (`@Binds fun bind(impl: WeaponRepositoryImpl): WeaponRepository`), which removes boilerplate and keeps constructor changes in one place.~~
  ✅ **Done (2026-10-07)** in `api-a`, `api-b` and `api-twitch`. Each `di/` module is an `abstract class` of `@Binds`, with a `companion object` `@Provides` only for the Retrofit-created `Api`.
- ~~**Don't make use cases `@Singleton`.** They are stateless, so unscoped (or `@Reusable`) is enough.~~
  ✅ **Done (2026-10-07).** Use cases are bound unscoped. Data sources, caches and repositories stay `@Singleton`.
- **Stop exposing `cache` from repositories.** `LoadWeaponListUseCase` reads `repository.cache` directly, which leaks the data layer. Add a `repository.observeWeaponList()` method, or go offline-first so the repository emits the cached value and then the network value.
- **Use Room for list caching.** Storing whole lists as JSON strings in Preferences DataStore doesn't scale and can't be queried. Room is a better fit, and Paging 3 could replace the manual paging in `WeaponListViewModel`.
- **Clean up `BaseViewModel`.** `baseScreenUiState` is declared as `var` but should be `val`. `permissionHandler` is field-injected through `lateinit`, so try constructor injection or a composable-level handler. `showErrorDialog` and `errorMessage` exist in `BaseScreenUiState` but nothing uses them, so either wire a shared error dialog into `BaseScreen` or remove them.
- **Move `domain/` out of the `api-*` modules.** Use cases currently live in the API modules, so features depend on data-layer modules. If you want strict Clean Architecture, split them into `domain-*` modules, or rename the API modules to `data-*` so the layering is honest.

### Build logic
- **Remove the duplication between conventions.** The flavor and `BuildConfig` code is copied between `base-convention` and `app-convention`. Pull it into a shared `configureFlavors()` extension in `buildlogic`.
- **Fix the hardcoded accessors in `DependencyExtension.kt`.** It imports generated accessors with hashed names (`gradle.kotlin.dsl.accessors._06e7…`), which break whenever Gradle regenerates them. Use `dependencies.add("implementation", …)` or `"ksp"(…)` instead.
- ~~**Make module wiring consistent.** `:core-navigation` and `:app` use `project(":x")` while others use `moduleImplementation("x")`. Pick one.~~
  ✅ **Done (2026-10-07).** Every module build file uses `moduleImplementation(projects.xxx)`, with type-safe project accessors (`ksp(projects.navigationProcessor)` in `:app`). The only `project(":navigation-processor")` left is inside `buildlogic`'s `composeDependencies()`, because an included build can't see the generated accessors.
- **Turn KSP incremental processing back on.** `ksp.incremental=false` slows builds. Once the processors declare their originating files correctly (`Dependencies(aggregating, sources)`), it can be re-enabled. Also consider enabling the Gradle configuration cache.
- **Trim `compose-convention`.** It adds Coil, Hilt-navigation and the `navigation-processor` to every compose module, including `core-ui` and `core-navigation`, which don't need them all.

### Navigation processor
- **Report errors through `KSPLogger.error(msg, symbol)` instead of `error(...)`.** That way problems such as "Multiple start destination" point to the offending file and line.
- **Check signatures at compile time.** The processor should verify that the annotated function takes exactly `(navigator: Navigator)` and that `routeParams` is `@Serializable`.
- **Handle multiple rounds.** `GraphProcessor` overwrites `featureGraphList` on every round; accumulate across rounds instead.
- **Add processor tests** using `kotlin-compile-testing-ksp`.

### UI / UX
- **Move hardcoded strings** (`"Retry"`, etc.) into `strings.xml` so the app can be localized.
- **Improve accessibility.** The `Uri*` components already have previews; add `contentDescription` on images and check touch-target sizes.
- **Add a dark theme and dynamic color** to `UrithiruTheme` if not already supported.
- **Remove the debug `println`** and use a logger such as Timber, gated to debug builds.

### Quality & tooling
- **Add tests.** The highest-value places to start:
  - unit tests for `BaseRepository.mapToEntity`, `resultFlow`, and the ViewModels (using a `TestDispatcherProvider`, Turbine and MockK/fakes)
  - Compose UI tests for the main flows
- **Add static analysis.** Use Detekt and ktlint (or Spotless), plus Android Lint with `warningsAsErrors` in CI.
- **Add CI.** A GitHub Actions workflow that builds every flavor and runs tests and lint on each PR.
- **Add a dependency-update bot.** Renovate or Dependabot can update `libs.versions.toml`.
- **Bring the README up to date** with the actual modules, `@FeatureRoute(graph = …)`, and the Gradle version, and link to this summary.

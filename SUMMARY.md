# Urithiru Project — Summary

A modular Android app template built with Clean Architecture, Jetpack Compose, Hilt, Retrofit and Coroutines/Flow. It ships as **Urithiru Showcase**: a live onboarding (text from **JSONBin**), a **DummyJSON** login, and four tabs (Catalog · Shop · Explore · Profile). Explore uses **Elden Ring** data from `eldenring.fanapis.com`; Profile shows the signed-in user (`auth/me`, with automatic token refresh), the theme and language settings, replay onboarding and logout. Shop is a placeholder for now. All UI text comes from one translation JSON (English and Bahasa Indonesia); the app starts in English and never assumes the device language. The plan and roadmap are in `FOR READING.md`.

- Package root: `com.septianbeneran.urithiru` for every module. A module's namespace is the root plus its module name, with `-` replaced by `.`
- SDK: compileSdk 37, minSdk 24, targetSdk 35, Java 21
- Build: Gradle 9.9.0, AGP 9.4.1, Kotlin 2.4.21, KSP 2.3.12, Compose BOM 2026.09.00, Hilt 2.60.1, Coil 3.6.3; compileSdk/targetSdk 37

---

## Module Map

Modules are grouped in folders: `core/` (`core`, `core-entity`, `core-navigation`, `core-ui`), `api/` (`api-*`) and `feature/` (`feature-*`); `app` and `navigation-processor` stay at the root. The Gradle paths stay flat (`:core-ui` lives in `core/core-ui`), so `projects.*` accessors, namespaces and packages don't include the folder. `settings.gradle.kts` maps each module to its folder with `includeModules("<cluster>", …)`.

| Module | Kind | Responsibility |
|---|---|---|
| `:app` | application | `UrithiruProject` (`@HiltAndroidApp`), `MainActivity`, and `NavHostChamber` (the root `NavHost`, which starts at `FeatureSplashNavGraph` and calls the generated `registerAllFeatureGraphs`) |
| `:buildlogic` | included build | Convention plugins, `AppConfig`, `ProductFlavor`, dependency bundles |
| `:core` | library | Base data/repository classes, `ApiResult`/`BaseState`/`AppError`, Retrofit and OkHttp DI, DataStore wrapper, dispatchers, permission handling, `AppSettings` (`settings/`: theme, language, onboarding done), the `Translator` and the bundled translation file (`localization/`, `assets/localization/translations.json`), and generic extensions in `util/extension/` (`StringExtensions`, `FloatExtensions`, `BufferExtensions`, `ExceptionExtensions`, `ResponseExtensions`, `ContextExtensions`) |
| `:core-entity` | library | Domain models: the compendium (`CompendiumKind`, sealed `CompendiumEntry` with `Boss`, `Weapon`, `Armor`, `Item`, `Location`, `Npc`, plus `CompendiumPage`, `CompendiumFeedPage`, `CompendiumSearchResult`, `Attributes`, `Scaling`, `StatValue`), `OnBoardingPageData`, `AuthSession`, `User`, `UserProfile` |
| `:core-navigation` | compose lib | `@FeatureRoute` and `@FeatureGraph` annotations, `@Serializable` graph objects and route params, the `Navigator` wrapper |
| `:core-ui` | compose lib | `BaseScreen` (plus `LocalBottomOverlayHeight`/`LocalScreenBottomPadding` in `base/ScreenInsets.kt`), `BaseViewModel`, `NonceObserver`, theme (`UrithiruTheme`, colors, typography), the `Uri*` design-system components, `localizedString` and `LocalTranslator` (`localization/`), the shared `common_*` string keys, and Compose/UI extensions in `util/extension/` (`ModifierExtensions`, `NavGraphBuilderExtensions`, `ThemeModeExtensions`, `ContextExtensions` with `withThemeMode`/`withLanguage`, `AppErrorExtensions`) |
| `:api-eldenring` | api lib | The Elden Ring **compendium** (Bosses, Weapons, Armors, Items, Locations, Npcs as `CompendiumKind`): `CompendiumApi` → `CompendiumRemoteDataSource` → `CompendiumRepository`. Each DTO implements `CompendiumResponse.mapToEntry()`, which also cleans the API's placeholders and misspelled regions (e.g. "Altus Pleateau"); each entity implements the sealed `CompendiumEntry` in `:core-entity`. Use cases: `GetCompendiumList`/`GetCompendiumDetail`; `GetCompendiumFeedPage`, which loads a page of several sections at once (each with its own page number) and mixes them (seeded, so the order is stable; a failed section doesn't fail the page and is asked for the same page again next time); and `SearchCompendium`, which groups matches by section, closest names first, plus a top result. Both share `loadSections` in `domain/get/CompendiumSections.kt`. No cache yet |
| `:api-jsonbin` | api lib | **JSONBin** onboarding pages (with a cache) |
| `:api-dummyjson` | api lib | **DummyJSON** login (`auth/login`), token refresh (`auth/refresh`) and the signed-in user (`auth/me`); the session (tokens + user) and a "session expired" flag are saved unencrypted in DataStore via `AuthCache`. A real app should encrypt them |
| `:feature-splash` | feature lib | Splash screen. Goes straight to the tabs if onboarding was completed; otherwise fetches the onboarding text from JSONBin and opens onboarding, even if the request fails |
| `:feature-onboarding` | feature lib | Four live demo pages (components, error handling, theme and language pickers, sign in) |
| `:feature-catalog` | feature lib | Catalog tab (home): every `Uri*` component grouped by section (`catalog/CatalogEntries.kt`). Each opens `CatalogComponentRoute(id)` with interactive demos (`screen/demo/`). Add a component by adding an entry and a demo function |
| `:feature-shop` | feature lib | Shop tab. Placeholder for DummyJSON products |
| `:feature-profile` | feature lib | Profile tab: user card and account details from `auth/me`, or a guest card with "Sign in"; `UriThemePicker` and `UriLanguagePicker`; replay onboarding; logout with a confirmation dialog |
| `:feature-auth` | feature lib | `LoginRoute`: username/password with `UriTextField`, a "Use demo account" shortcut, and friendly errors. `LoginRoute(returnToPrevious = true)` goes back to the screen that opened it (Profile) instead of home |
| `:feature-explore` | feature lib | Explore tab: a mixed compendium feed (`ExploreRoute`/`ExploreViewModel`): section chips (All, Bosses, Weapons, Armor, Items, Locations, NPCs), a Featured carousel, a two-column staggered grid with a card style per type (`screen/feed/CompendiumCards.kt`), "Around <region>" rows on All (bosses and locations already loaded, grouped by region once a region has 4), infinite scroll and pull-to-refresh (keeps the feed visible, reshuffles it, and shows a `UriToast` if the refresh fails). Searching on All shows a top result and one row per section with "See all", which opens that section's chip with the same query. Every card opens `CompendiumDetailRoute(kind, id)`: a hero image, a sheet with type tags, stat tiles, and sections per type (stat bars, scaling and requirement chips, drops, effect callout, NPC quote) in `screen/detail/` |
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
- **`AndroidExtension.kt`**: `configureAndroid(project)` holds the Android setup shared by the app and every library (compileSdk, minSdk, Java 21, BuildConfig/resValues, and the product flavors). Both `base-convention` and `app-convention` call it, so flavor logic lives in one place.
- **`base-convention`**: Android library, namespace `com.septianbeneran.urithiru.<module.name>`, `configureAndroid`, the kotlinx-serialization compiler plugin and kotlinx-serialization-json, plus JUnit and AndroidX test. No Hilt or DataStore: `core-entity` uses it as is.
- **`hilt-convention`**: KSP, the Hilt plugin, Hilt and its compiler. Included by `api-convention` and `feature-convention`, and applied directly by `core` and `core-ui` (`BaseViewModel` uses field injection). DataStore is added only in `:core`, the one module that imports it.
- **`api-convention`**: base and Hilt conventions plus Retrofit. It also loads any keys in the module's `microservice.properties` into `BuildConfig`. Endpoint paths don't go there: each `XRemoteDataSourceImpl` passes them to the `@Url` methods as literal strings (e.g. `"bosses"`, `"bosses/$id"`).
- **`compose-convention`**: base convention plus Compose (BOM, UI, Material3), navigation-compose and activity-compose. Compose tooling (previews, layout inspector) is `debugImplementation`, so it doesn't ship in release builds. Used by `core-ui` and `core-navigation`. (`core-ui` also adds Coil itself, for image-loading components.)
- **`feature-convention`**: compose and Hilt conventions plus Hilt-navigation (`hiltViewModel()`), Coil, and `ksp(project(":navigation-processor"))`. Used by every `feature-*` module.
- **`app-convention`**: application setup through `configureAndroid` (flavors also get their `applicationIdSuffix`), plus `targetSdk` and `versionCode`.
- `DependencyExtension.kt` adds dependencies by configuration name (`"implementation"(…)`, `"ksp"(…)`), never through Gradle's generated hashed accessor imports.
- `gradle.properties` enables incremental KSP (the default) and the configuration cache.

**Product flavors** (dimension `environment`): `dev` (default, `.dev`), `uat` (`.uat`), `beta` (`.beta`), `prod`. Each flavor reads `productFlavorProperties/<flavor>.properties` and turns every key into a `BuildConfig` String field. The keys are `APP_NAME`, `ELDEN_RING_BASE_URL`, `JSON_BIN_BASE_URL` and `DUMMY_JSON_BASE_URL`.

`moduleImplementation(projects.xxx)` (`GradleExtension.kt`) takes a type-safe project accessor. These are enabled by `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` in `settings.gradle.kts` and generated from its `include(...)` list, so only modules that exist can be referenced, and the IDE autocompletes them. Adding a module to `settings.gradle.kts` creates its accessor (`api-dummyjson` → `projects.apiDummyjson`). The root project is named `Urithiru`, because accessors require a name without spaces. If the root `extra` defines a version for that module name, it resolves to the Maven artifact `com.septianbeneran.urithiru:name:version` instead, which lets a module be swapped for a published version.

---

## Architecture & Data Flow

```
Screen ─► ViewModel ─► UseCase ─► Repository ─► RemoteDataSource ─► Retrofit Api
                                      └──► Cache (BaseDataStore / kotlinx JSON)
```

**Core building blocks (`:core`)**
- `BaseDataSource.getResult { api.call() }` turns a Retrofit `Response<T>` into `ApiResult.Success` or `ApiResult.Error` with a typed `AppError` (HTTP status or exception mapped once, `CancellationException` rethrown).
- `resultFlow(networkCall, dispatcher)` emits `Loading` and then the result, running on `io()`.
- `BaseRepository.mapToEntity(transform, saveResult)` maps the DTO to an entity. If `saveResult` is given, it saves the result to the cache before emitting `Success` (in `NonCancellable`, so the write finishes even if the screen closes). A mapping failure becomes `AppError.Parse`; a failed save becomes `AppError.Unknown`.
- `ApiDto<T>(success, count, data)` is the Elden Ring envelope. `JsonBinDto` is the JSONBin envelope.
- `BaseState`: `StateInitial | StateLoading | StateSuccess | StateFailed` (UI-side state).
- `NetworkModule` provides one `Retrofit` per backend, using the qualifiers `@EldenRingNetwork`, `@JsonBinNetwork` and `@DummyJsonNetwork`. Every `Retrofit` uses the kotlinx-serialization converter. The shared `OkHttpClient` adds `LogcatInterceptor` in debug builds and [Chucker](https://github.com/ChuckerTeam/chucker) as a network interceptor, so every call (including the `Authorization` header and a 401 followed by its retry) can be inspected on the device. `:core` uses `debugImplementation` for Chucker and `releaseImplementation` for its `library-no-op`, which passes requests through and adds no UI.
- `SerializationModule` provides the single shared `Json` instance (`ignoreUnknownKeys = true`, `explicitNulls = false`), used by both Retrofit and `BaseDataStore`. Gson is not used anywhere.
- JSON models must be `@Serializable`: DTOs, the `ApiDto`/`JsonBinDto` envelopes, and any entity stored with `saveObject`. Rename fields with `@SerialName`, not `@SerializedName`.
- `BaseDataStore` is a Preferences DataStore with primitive save/read and `saveObject`/`readObject` through kotlinx-serialization (the shared `Json`).

**Per-API module layout** (for example `api-eldenring`):
```
data/remote/api/         Retrofit interface (@GET with @Url; the data source passes the endpoint path)
data/remote/dto/         Response DTOs with mapToEntity functions (for example `mapToEntry()` in api-eldenring)
data/remote/service/     XRemoteDataSource + Impl (extends BaseDataSource)
data/local/              XCache + Impl (wraps BaseDataStore)
repository/              XRepository + Impl (extends BaseRepository, exposes `cache`)
domain/get/              Get*UseCase: network-backed Flow<ApiResult<Entity>>
domain/load/             Load*UseCase: cache-backed Flow<Entity>
domain/clear/            Clear*UseCase: suspend, removes saved data (e.g. logout)
di/                      Hilt modules: @Binds for Impl → interface, @Provides only for the Retrofit Api
```

**Presentation pattern (`:core-ui` + features)**
- Each screen has a `stateaction/` file containing `XUiState` (data class), `XAction` (sealed interface, an MVI-style intent), and `XNonce : BaseNonce` (one-shot events such as navigation).
- The ViewModel extends `BaseViewModel` and exposes `uiState: StateFlow` and `onAction(action)`. It uses:
  - `collectApi(flow, isCentralLoading, onLoading, onError, onSuccess)`, which also toggles the global loading dialog
  - `collectLocalData(flow) { … }` for cache flows
  - `sendNonce(nonce)`, which sends to a `Channel` that is collected by `NonceObserver` in the composable
- `BaseScreen(viewModel, topAppBarArgs, onBack, …) { properties -> }` handles the central loading dialog, the back handler, system bar padding, the optional `UriTopAppBar`, and runtime permissions (`properties.handlePermission`). It keeps content clear of the floating bottom bar; a scrolling tab root passes `drawBehindBottomOverlay = true` and adds `LocalScreenBottomPadding.current` to its list's bottom padding so content scrolls behind the bar.
- All UI text goes through `localizedString(R.string.x, args…)`; see Text & Localization below.

---

## Navigation Code Generation (KSP)

1. Declare a `@Serializable` graph object (`core-navigation/.../graph`) and a route (`.../routeparams/<feature>`). Use an `object`, or a `data class` when the route takes arguments.
2. Annotate the screen composable:
   ```kotlin
   @FeatureRoute(routeParams = ExploreRoute::class, graph = FeatureExploreNavGraph::class) // set graph only on the start destination
   @Composable fun ExploreRoute(navigator: Navigator) { … }
   ```
3. **`FeatureProcessor`** (runs in feature modules):
   - generates one `NavGraphBuilder` extension per route
   - generates `fun NavGraphBuilder.featureExploreNavGraph(navigator)` annotated `@FeatureGraph`, in package `com.septianbeneran.urithiru.feature.graph`
   - fails the build if a module has zero or more than one start destination
4. **`GraphProcessor`** (runs in the app, enabled by `ksp { arg("isAppModule","true") }`) scans the `...feature.graph` package. It generates `registerAllFeatureGraphs(navigator)` in `com.septianbeneran.urithiru.graph`.
5. `NavHostChamber` calls `registerAllFeatureGraphs`, so new screens need no manual registration.

**Current routes / app flow**
```
Splash (FeatureSplashNavGraph start)
  ├─► Tabs, when onboarding was completed before
  └─► OnBoarding (FeatureOnboardingNavGraph start) ─┬─► Login (FeatureAuthNavGraph start) ─► Tabs
                                                    └─► "Continue as guest" ─────────────────► Tabs

Tabs (TopLevelDestination in :app; a floating UriNavigationBar drawn over the screens on phones, a Material rail in NavigationSuiteScaffoldLayout on larger screens):
  Catalog (home) · Shop · Explore (mixed feed) ─► CompendiumDetail(kind, id) · Profile
```
`Navigator.navigateToHome()` opens the first tab and clears the back stack, so Back from there leaves the app. `Navigator.navigateToTopLevel(graph)` switches tabs; each tab keeps its own history, and Back from another tab returns to Catalog. Shop is a placeholder for now.

**Onboarding pages** (`feature-onboarding/screen/demo/`): each page is a live demo, not an image.
1. `ComponentsDemo`: real `Uri*` components animate in one by one.
2. `ErrorHandlingDemo`: a "Go offline" switch runs a simulated request in the ViewModel and shows the skeleton, `UriErrorState` with Retry, or data.
3. `PreferencesDemo`: `UriThemePicker` (Light / Dark / Match system) and `UriLanguagePicker`, both saved through `AppSettings`.
4. `SignInDemo`: opens `LoginRoute`, or "Continue as guest". Shows "Continue as <name>" when a session exists.

Titles and descriptions come from JSONBin (server-driven) and are shown as-is, untranslated; `OnBoardingDefaults` in the feature (translation keys) is used for any page the bin doesn't provide. Choosing Sign in or Continue sets `hasCompletedOnboarding`, so later launches skip onboarding. Profile's "Replay onboarding" replays it.

---

## Design System (`core-ui/component`)
`UriAvatar`, `UriBadge`, `UriBanner`, `UriButton`, `UriCheckBox`, `UriDialog`, `UriDivider`, `UriDot`, `UriEmptyState`, `UriErrorState`, `UriImage`, `UriLanguagePicker`, `UriListFooter`, `UriListItem`, `UriLoader`, `UriMediaPlaceHolder`, `UriNavigationBar`, `UriProgressBar`, `UriRadioButton`, `UriSearchBar`, `UriSkeleton`, `UriStepper`, `UriTextField`, `UriThemePicker`, `UriToast`, `UriTopAppBar`, `CentralLoadingDialog`. The theme and language pickers share the internal `UriChoiceOption` card. The theme lives in `core-ui/theme` (with custom fonts under `res/font`). `UrithiruTheme.kt` maps every Material 3 color role to the Urithiru palette for light and dark, and `Typography.kt` maps the Material type roles in size order. `ThemeMode` (`System`/`Light`/`Dark`, in `:core` `settings/`) is a user setting saved by `AppSettings` (`:core`, DataStore), which also holds `hasCompletedOnboarding`. `UrithiruProject.defaultThemeMode` is used until the user picks one. `:app` provides `UrithiruProject.defaultThemeMode` to `AppSettings` through the `@DefaultThemeMode` qualifier, and `AppSettings.effectiveThemeMode` is the saved mode or that default, so the theme picker in Profile and onboarding always shows the mode in use. `UrithiruProject` reads that mode once at startup (`runBlocking`, before the first frame) into `currentThemeMode`; `MainActivity` applies it to the system bars, the activity's resources (`attachBaseContext`) and, on Android 12+, the launch window (`UiModeManager.setApplicationNightMode`). When the saved mode changes, `MainActivity` recreates itself, like AppCompat does for night mode. `MainActivity` wraps the app in a `Surface` with the theme background, and `values-night/themes.xml` gives the window a dark parent.

---

## Text & Localization

- **Where text lives:** `core/core/src/main/assets/localization/translations.json`: `{"version", "defaultLanguage": "en", "languages": {"en": {...}, "id": {...}}}`. Values are ICU MessageFormat: `{0}` for arguments, `{0, plural, one {# drop} other {# drops}}` for counts.
- **`strings.xml` only holds keys:** each entry's value is its camelCase name (`<string name="explore_featured">exploreFeatured</string>`), which keeps compile-time `R.string` ids. Each module owns its keys with a module prefix; keys used by more than one module are `common_*` in `:core-ui`.
- **Reading text:** `localizedString(R.string.x, args…)` (`:core-ui`) reads the key and translates it in the current language. `localizedString(text, namedArgs)` translates a string that didn't come from a resource (e.g. a backend's `errorTitle`): a key in the JSON is translated, anything else is shown exactly as given. Outside Compose, inject `Translator` (`:core`).
- **Loading:** `AssetTranslator` reads the bundled file once per launch, or a downloaded copy in `filesDir/localization/translations.json` when its `version` is higher, so a new file only applies on the next launch. No download exists yet.
- **Language setting:** `AppSettings.effectiveLanguage` is the saved language (`saveLanguage`) while the file has it, otherwise the file's `defaultLanguage` (`en`); the device language is never used. `UrithiruProject` reads it at startup into `currentLanguage`; `MainActivity` applies it in `attachBaseContext` (`withLanguage`, `Locale.setDefault`) and recreates itself when it changes. `UriLanguagePicker` lists the file's languages, each named by its `languageName` key.
- **Safety net:** `TranslationFileTest` (`:core`) fails if a `strings.xml` key is missing in any language, a JSON key is unused, a language's arguments differ from English, or a resource's value isn't its camelCase name.

---

## How-To Cheatsheet

**Add an API endpoint:** add a DTO, then the Retrofit method (`@Url` path). Then add the DataSource method (`getResult { … }`, passing the endpoint path as a string), the Repository method (`resultFlow(...).mapToEntity(...)`), and a UseCase interface with its Impl. Wire them in the module's `di/`.

**Add a new backend:** add `*_BASE_URL` to every `productFlavorProperties/*.properties`, add a qualifier annotation in `core/annotation`, and add a `Retrofit` provider in `NetworkModule`. Then create a new `api-*` module using `api-convention`, in `api/`, and add it to the `api` `includeModules` call in `settings.gradle.kts` and to `app/build.gradle.kts`.

**Add a screen:** add the route (plus a graph object if this is a new feature) in `core-navigation`, add the `@FeatureRoute` composable, ViewModel and stateaction in the feature module, then build.

**Add text:** add `<string name="x_thing">xThing</string>` to the module's `strings.xml` (or `:core-ui` as `common_*` if several modules use it), add `xThing` to every language in `translations.json`, and read it with `localizedString(R.string.x_thing)`. Run `./gradlew :core:testDevDebugUnitTest` to check nothing is missing.

**Add a language:** add a block such as `"pt-BR": { "languageName": "Português (Brasil)", … }` with every key to `translations.json` and bump `version`. It appears in the language picker automatically.

**Add a feature module:** use `feature-convention` and depend on `core`, `core-entity`, `core-navigation`, `core-ui` and the needed `api-*`. Create it in `feature/`, add it to the `feature` `includeModules` call in `settings.gradle.kts`, and to the `app` dependencies.

---

## Observations / Known Issues (as of 2026-11-01)

> On the `improvement` branch, the broken `WeaponRepository` import is fixed and every package is now under `com.septianbeneran.urithiru`. See also `CLAUDE.md` (conventions) and `FOR READING.md` (roadmap and ideas).

- The unused `api-twitch` module (Twitch OAuth `client_credentials` token) and the unused `@IgdbNetwork` Retrofit were removed on 2026-11-03.
- The splash screen hardcodes the JSONBin `binId` in `SplashScreenViewModel` (`ONBOARDING_BIN_ID`).
- `UriAvatar`, `UriBadge`, `UriBanner`, `UriButton`, `UriDot`, `UriListItem`, `UriLoader`, `UriMediaPlaceHolder`, `UriProgressBar` and `UriToast` still reference palette colors directly. Most are brand accents (blue, success, error) that read fine on both themes, but they haven't all been checked in dark mode. `UriRadioButton`, `UriCheckBox`, `UriStepper`, `UriTopAppBar` and `UriDialog` now use theme colors.
- **Auth (`api-dummyjson`):** `AuthApi` (login, refresh) uses the plain DummyJSON client. `UserApi` (`auth/me`) uses a client with `AuthInterceptor` (adds `Authorization: Bearer`) and `TokenAuthenticator` (on a 401, `AuthRepository.refreshAccessToken` calls `auth/refresh` behind a `Mutex`, saves the new tokens and the request is retried once; a rejected refresh token clears the session and sets a saved "session expired" flag, so Profile shows a warning toast until the user signs in again or closes it). DummyJSON answers a tampered token with 500, not 401, so only a real expiry triggers a refresh.
- Explore's "All" view loads 6 entries per section per page through `GetCompendiumFeedPageUseCase`, keeps a page number per section (`ExploreUiState.kindPages`), retries a failed section on the next load and stops asking a section once it has ended. A single section loads 20 at a time (`ExploreViewModel.PAGE_SIZE`) and stops on a short page or once `total` entries are loaded. The API's `total` is the size of the whole section, even for a name search.
- Real unit tests: `api-eldenring` (DTO mapping, the feed mixer, compendium search), `api-dummyjson` (`AuthRepositoryImplTest`, token refresh), `core` (`TranslationCatalogTest`, `TranslationFileTest`) and `feature-explore` (`ExploreViewModelTest`, using `kotlinx-coroutines-test` to replace the Main dispatcher). Other modules still only have the default `ExampleUnitTest` and `ExampleInstrumentedTest` stubs. Every module now gets JUnit and AndroidX test from `testDependencies()` in the convention plugins, so they compile and run.

---

## Suggested Improvements

> **Status:** items that are ~~crossed out~~ and marked ✅ are done. Everything else is still open.

### Correctness & robustness
- ~~**Cancel in-flight requests in `BaseViewModel.collectApi`.** Each call starts a new `viewModelScope.launch` and keeps no handle to it. When a user taps search several times quickly, those requests run at the same time and can append results out of order. Return the `Job` and cancel the previous one for the same key, or switch to a `MutableStateFlow` of queries combined with `flatMapLatest`.~~
  ✅ **Done (2026-10-27).** `collectApi` takes an optional `key` and returns the `Job`. A new call with the same key cancels the previous one. `ExploreViewModel` uses it for search, section switches and paging.
- ~~**Remove the `TODO()` calls in `TwitchRepositoryImpl`** (`saveTwitchToken` and `loadTwitchToken`). These functions crash at runtime if anything calls them.~~
  ✅ **Done (2026-11-01).** Both are implemented with `TwitchCache`. The whole module was later removed (2026-11-03).
- ~~**Pick one JSON library.** Retrofit uses `GsonConverterFactory`, but the DTOs carry kotlinx `@Serializable` and some also carry Gson `@SerializedName`. The kotlinx converter is a dependency but is never used. Gson ignores Kotlin nullability and default values, so a missing field can silently become `null` inside a non-null type. Standardizing on kotlinx-serialization (for both Retrofit and `BaseDataStore`) would avoid that.~~
  ✅ **Done (2026-10-07).** Retrofit and `BaseDataStore` both use kotlinx-serialization through one shared `Json`. Gson and the unused JakeWharton converter are removed. `base-convention` now applies the serialization compiler plugin; before this, `@Serializable` in `api-*` and `core-entity` generated nothing.
- ~~**Replace the magic error code `6969`.** Use typed errors instead, for example `sealed class AppError { Network, Http(code), Parse, Unknown }`, so the UI can show meaningful messages and offer a retry only when it makes sense.~~
  ✅ **Done (2026-10-27).** `AppError` in `:core` replaces `ErrorResponse`. `BaseDataSource.getResult` maps exceptions and HTTP codes to it and rethrows `CancellationException`. `core-ui` adds `AppError.toMessage()` (string resources), `UriErrorState`, `UriEmptyState` and `UriSkeleton`/`UriSkeletonListItem`. Splash now shows an error with retry instead of getting stuck. A failed *next* page now shows `UriListFooter` with Retry, and the list only auto-loads after a successful page, so it no longer retries on every recomposition.
- **Get the page size from one place.** `isEndReached = size < 20` assumes the API returns 20 items, but no `limit` is sent with the request. Send `limit` explicitly and compare against the same constant.
- **Keep secrets out of source.** API keys and client secrets should come from `local.properties` or CI secrets and be injected into `BuildConfig`, not hardcoded. The JSONBin `binId` should move to `microservice.properties`.

### Security & release readiness
- ~~**Limit `LogcatInterceptor` to debug builds.** Today it logs every header, including `Authorization`, and full bodies in all flavors, `prod` included. Add it only when `BuildConfig.DEBUG` is true, or redact sensitive headers.~~
  ✅ **Done (2026-10-27).** `NetworkModule` adds it only when `BuildConfig.DEBUG` is true. Headers are not redacted, so debug logs still show `Authorization`.
- **Add a `buildTypes { release { isMinifyEnabled = true; isShrinkResources = true } }` block** with R8 keep rules for Retrofit and kotlinx-serialization models. No minify or shrink setup exists yet.
- ~~**Set `targetSdk` closer to `compileSdk`.** `targetSdk` is 35 while `compileSdk` is 37, and Play's target API requirements will catch up.~~
  ✅ **Done (2026-10-27).** `targetSdk` is 37.

### Architecture & DI
- ~~**Make the DI style consistent.** Classes have `@Inject constructor` *and* are also built by hand in `@Provides` methods. Use `@Binds` abstract modules instead (`@Binds fun bind(impl: WeaponRepositoryImpl): WeaponRepository`), which removes boilerplate and keeps constructor changes in one place.~~
  ✅ **Done (2026-10-07)** in `api-eldenring`, `api-jsonbin` and `api-twitch`. Each `di/` module is an `abstract class` of `@Binds`, with a `companion object` `@Provides` only for the Retrofit-created `Api`.
- ~~**Don't make use cases `@Singleton`.** They are stateless, so unscoped (or `@Reusable`) is enough.~~
  ✅ **Done (2026-10-07).** Use cases are bound unscoped. Data sources, caches and repositories stay `@Singleton`.
- ~~**Stop exposing `cache` from repositories.** `LoadWeaponListUseCase` reads `repository.cache` directly, which leaks the data layer.~~
  ✅ **Done (2026-11-01)** for `api-eldenring`: the unused weapon cache and `LoadWeaponListUseCase` were removed when weapons moved into the compendium. If caching comes back (plan step 9), have the repository emit the cached value and then the network value instead of exposing `cache`.
- **Use Room for list caching.** Storing whole lists as JSON strings in Preferences DataStore doesn't scale and can't be queried. Room is a better fit, and Paging 3 could replace the manual paging in `ExploreViewModel`.
- **Clean up `BaseViewModel`.** `baseScreenUiState` is declared as `var` but should be `val`. `permissionHandler` is field-injected through `lateinit`, so try constructor injection or a composable-level handler. `showErrorDialog` and `errorMessage` exist in `BaseScreenUiState` but nothing uses them, so either wire a shared error dialog into `BaseScreen` or remove them.
- **Move `domain/` out of the `api-*` modules.** Use cases currently live in the API modules, so features depend on data-layer modules. If you want strict Clean Architecture, split them into `domain-*` modules, or rename the API modules to `data-*` so the layering is honest.

### Build logic
- ~~**Remove the duplication between conventions.** The flavor and `BuildConfig` code is copied between `base-convention` and `app-convention`. Pull it into a shared `configureFlavors()` extension in `buildlogic`.~~
  ✅ **Done (2026-10-07).** `buildlogic/AndroidExtension.kt` has `configureAndroid(project)`, which covers SDKs, Java 21, build features and flavors. `applicationIdSuffix` is only set for the app. The generated applicationIds and `BuildConfig` fields were checked to match the old output for dev, uat and prod.
- ~~**Fix the hardcoded accessors in `DependencyExtension.kt`.** It imports generated accessors with hashed names (`gradle.kotlin.dsl.accessors._06e7…`), which break whenever Gradle regenerates them. Use `dependencies.add("implementation", …)` or `"ksp"(…)` instead.~~
  ✅ **Done (2026-10-07).** All helpers use `"implementation"(…)` and `"ksp"(…)`.
- ~~**Make module wiring consistent.** `:core-navigation` and `:app` use `project(":x")` while others use `moduleImplementation("x")`. Pick one.~~
  ✅ **Done (2026-10-07).** Every module build file uses `moduleImplementation(projects.xxx)`, with type-safe project accessors (`ksp(projects.navigationProcessor)` in `:app`). The only `project(":navigation-processor")` left is inside `buildlogic`'s `composeDependencies()`, because an included build can't see the generated accessors.
- ~~**Turn KSP incremental processing back on.** `ksp.incremental=false` slows builds. Once the processors declare their originating files correctly (`Dependencies(aggregating, sources)`), it can be re-enabled. Also consider enabling the Gradle configuration cache.~~
  ✅ **Done (2026-10-07).** Each generated route declares its screen file as its origin (isolating), and the feature graph declares *all* screen files (aggregating), so editing any screen reprocesses the whole graph. Tested incrementally: editing a non-start screen, adding and removing a screen, and adding and removing a feature graph that `:app`'s registry must pick up. The configuration cache is also on, and it was checked to invalidate when a flavor `.properties` file changes.
- ~~**Trim `compose-convention`.** It adds Coil, Hilt-navigation and the `navigation-processor` to every compose module, including `core-ui` and `core-navigation`, which don't need them all.~~
  ✅ **Done (2026-10-07).** The feature-only extras moved to the new `feature-convention`. `core-ui` keeps Coil explicitly for future image components. The unused `kotlin-parcelize` plugin was dropped.

### Navigation processor
- **Report errors through `KSPLogger.error(msg, symbol)` instead of `error(...)`.** That way problems such as "Multiple start destination" point to the offending file and line.
- **Check signatures at compile time.** The processor should verify that the annotated function takes exactly `(navigator: Navigator)` and that `routeParams` is `@Serializable`.
- **Handle multiple rounds.** `GraphProcessor` overwrites `featureGraphList` on every round; accumulate across rounds instead.
- **Add processor tests** using `kotlin-compile-testing-ksp`.

### UI / UX
- ~~**Move hardcoded strings** (`"Retry"`, etc.) into `strings.xml` so the app can be localized.~~
  ✅ **Done (2026-11-01).** All UI text is in the translation JSON (English and Bahasa Indonesia), read through `localizedString`, with a language picker in onboarding and Profile. The Indonesian text still needs a native speaker's review.
- **Improve accessibility.** The `Uri*` components already have previews; add `contentDescription` on images and check touch-target sizes.
- ~~**Add a dark theme and dynamic color** to `UrithiruTheme` if not already supported.~~
  ✅ **Dark theme done (2026-10-27).** Every color role is mapped for light and dark, the background is painted, and `ThemeMode` can force a mode. Dynamic color (Material You wallpaper colors) is still open, if wanted.
- ~~**Remove the debug `println`**~~ ✅ **Done (2026-10-27).** Still open: use a logger such as Timber, gated to debug builds.

### Quality & tooling
- **Add tests.** The highest-value places to start:
  - unit tests for `BaseRepository.mapToEntity`, `resultFlow`, and the ViewModels (using a `TestDispatcherProvider`, Turbine and MockK/fakes)
  - Compose UI tests for the main flows
- **Add static analysis.** Use Detekt and ktlint (or Spotless), plus Android Lint with `warningsAsErrors` in CI.
- **Add CI.** A GitHub Actions workflow that builds every flavor and runs tests and lint on each PR.
- **Add a dependency-update bot.** Renovate or Dependabot can update `libs.versions.toml`.
- ~~**Bring the README up to date** with the actual modules, `@FeatureRoute(graph = …)`, and the Gradle version, and link to this summary.~~
  ✅ **Done (2026-10-27).** Also fixed the base URL and endpoint steps to match `productFlavorProperties` and `microservice.properties`.

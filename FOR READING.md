# For Reading: What To Do Next

Notes for reading on your phone (before sleeping, while commuting) and for working away from the main laptop without AI. Each section starts with its status. For how the code is built, see `SUMMARY.md`; for the rules Claude follows, see `CLAUDE.md`.

Last updated: 2026-11-01, branch `improvement` (not merged into `main` yet).

---

## 1. Where things stand

The app is becoming **Urithiru Showcase**: a working template app that shows off the components, the navigation, and how easy it is to add a feature.

**What works today:**
- **First launch:** splash → a 4-page *live* onboarding → sign in (or continue as guest) → the tabs. Later launches skip onboarding.
- **Tabs:** Catalog · Shop · Explore · Profile, as a bottom bar on phones and a rail on tablets. The bar only shows on a tab's root screen.
- **Catalog:** every `Uri*` component, each with interactive demos.
- **Explore:** a mixed feed of the Elden Ring compendium (bosses, weapons, armor, items, locations, NPCs) with section chips, a Featured carousel, a staggered grid with a card style per type, grouped search (top result plus a row per section) and paging. Every entry opens one shared detail screen.
- **Login:** DummyJSON demo account (`emilys` / `emilyspass`). The session is saved on the device.
- **Profile:** the signed-in user and account details from `auth/me` (the access token is refreshed automatically when it expires), or a guest card with "Sign in". Also the theme setting, replay onboarding and logout.
- **Theme:** Light / Dark / Match system, picked in onboarding or Profile and saved.
- **Language:** English or Bahasa Indonesia, picked in onboarding (page 3) or Profile and saved. The app starts in English and never follows the device language. All text comes from one translation JSON.
- **Errors:** typed `AppError`, with error, empty, loading and "couldn't load more" states everywhere.

**Still a placeholder:** Shop.

---

## 2. The plan: Urithiru Showcase

| Tab | Shows off | API | Status |
|---|---|---|---|
| **Catalog** (home) | Every component, every state | none | ✅ done |
| **Shop** | Paging, search, category filters, detail, a cart saved offline | DummyJSON products | ⏳ next |
| **Explore** | A second, separate API in its own module | Elden Ring | ✅ list + detail |
| **Profile** | Signed-in user, settings, logout, token refresh | DummyJSON auth | ✅ done |

**Why DummyJSON:** it's free, needs no key, and covers what a real app needs: login with access and refresh tokens, paging (`limit` / `skip`), search, categories, and fake writes (POST/PUT/DELETE return real responses but save nothing).

**Why three API modules** (`api-dummyjson`, `api-eldenring`, `api-jsonbin`): to prove that each backend lives in its own module with the same layering.

---

## 3. Next up

### Shop tab (`feature-shop` + new endpoints in `api-dummyjson`)
1. **Product list:** `GET products?limit=20&skip=0`. Reuse the weapon list's pattern: `collectApi` with a `key`, `UriListFooter`, skeleton rows on the first load.
2. **Search:** `GET products/search?q=…`, using `UriSearchBar`.
3. **Category chips:** `GET products/categories`, then `GET products/category/{slug}`. Needs a new **`UriChip`** component in `core-ui`, plus a Catalog entry for it.
4. **Detail:** `GET products/{id}`, with images, price, rating and an "Add to cart" button.
5. **Cart (later):** saved offline in **Room**. That's the moment to add Room to the project, and to fix the "repositories expose their cache" issue (see section 6).

### Profile tab: ✅ done (2026-10-31)
Ideas for later:
- **See the token refresh happen:** sign in with a short token. Pass `expiresInMins = 1` in `LoginRequest` (it has to be passed explicitly, because the shared `Json` doesn't encode default values), wait a minute, reopen the app and watch Logcat for `auth/refresh`.

---

## 4. How the app works now (short version)

**Navigation**
- One `NavHost`, in `:app` (`NavHostChamber.kt`). Every feature registers its screens with `@FeatureRoute`, and KSP generates the graphs.
- `navigator.navigate(SomeRoute(...))` opens a screen.
- `navigator.navigateToHome()` opens the first tab and clears everything before it. Splash, onboarding and login use it.
- `navigator.navigateToTopLevel(graph)` switches tabs. Each tab keeps its own history, and Back from another tab returns to Catalog.

**The bottom bar**
- The tab list is `TopLevelDestination` in `:app`.
- On phones the bar is `UriNavigationBar` (`core-ui`): a floating pill with a sliding indicator and springy icons. Restyle it with `NavigationBarStyle` in `NavHostChamber.kt` (shape, margins, elevation, labels, springs, haptics), or switch to `UriNavigationBarDefaults.Docked` for a classic edge-to-edge bar. Colors come from `UriNavigationBarDefaults.colors()`. The bar is drawn over the screens, so Explore, Catalog and Profile scroll behind it (`BaseScreen(drawBehindBottomOverlay = true)` plus `LocalScreenBottomPadding`); other screens are padded to stay clear of it. Larger screens keep the Material rail.
- The bar shows only when **every visible screen is a tab root**, so it comes back only after a back animation has finished. Showing it mid-animation resized the screens while they slid; that was the "weird back animation".

**Animations**
- Opening a screen slides in from the right, and Back slides it out to the right (150 ms both ways, `SLIDE_DURATION_MS` in `NavHostChamber.kt`).
- Moving between two tab roots **crossfades** (100 ms, `TAB_FADE_DURATION_MS`), because a slide misbehaved there.

**Theme**
- `ThemeMode` is saved by `AppSettings` (in `:core`).
- `UrithiruProject.defaultThemeMode` applies until the user picks one. Change it to force a mode while developing. `:app` hands it to `AppSettings` (`@DefaultThemeMode`), so screens read `AppSettings.effectiveThemeMode` and the picker always shows the mode actually in use.
- Every Material color role is mapped in `UrithiruTheme.kt`. **If something looks purple, a role is missing there.**

**Text and language**
- Real text lives in **one file**: `core/src/main/assets/localization/translations.json`, with a block per language (`en`, `id`).
- `strings.xml` files only hold **keys**: `<string name="explore_featured">exploreFeatured</string>`. The value is the name in camelCase.
- Screens read text with `localizedString(R.string.explore_featured)`. Never use `stringResource`.
- Placeholders are `{0}`, `{1}`; counts are `{0, plural, one {# drop} other {# drops}}`.
- Backend text (e.g. an error title) goes through `localizedString(text)`: if it's a key in the JSON it's translated, otherwise it's shown exactly as sent. A misspelled key shows up on screen as-is, which makes it easy to spot.
- The language is saved by `AppSettings`. English applies until the user picks one. `MainActivity` applies it and redraws when it changes.
- The JSON is read once per launch. A newer downloaded copy (not built yet) would only apply on the next launch.

**Errors**
- `BaseDataSource.getResult` turns every failure into an `AppError`.
- The UI shows `UriErrorState` (full screen), `UriListFooter` (when the next page fails) or `UriEmptyState`.
- `AppError.isRetryable` decides whether a Retry button appears.

---

## 5. Recipes (do these without AI)

### Add a screen to an existing feature
1. Add a route in `core-navigation/.../routeparams/feature_x/`:
   ```kotlin
   @Serializable data class ThingDetailRoute(val id: String)
   ```
2. Write the screen in the feature:
   ```kotlin
   @FeatureRoute(routeParams = ThingDetailRoute::class)
   @Composable fun ThingDetailRoute(navigator: Navigator) { … }
   ```
   It must take exactly `(navigator: Navigator)`.
3. ViewModel: `@HiltViewModel`, extending `BaseViewModel`. Read the argument with `savedStateHandle.toRoute<ThingDetailRoute>().id`.
4. Open it with `navigator.navigate(ThingDetailRoute("42"))`.
5. Build. If KSP output looks stale: `./gradlew clean assembleDevDebug`.

### Add a new tab
1. Create a module by copying `feature-catalog`'s `build.gradle.kts`, `.gitignore`, `*.pro` files and `src/main/AndroidManifest.xml`.
2. Add `include(":feature-x")` to `settings.gradle.kts`, and `moduleImplementation(projects.featureX)` to `app/build.gradle.kts`.
3. Add a graph object `FeatureXNavGraph` in `core-navigation/graph/`.
4. Give the start screen `@FeatureRoute(routeParams = XRoute::class, graph = FeatureXNavGraph::class)`.
5. Add one line to `TopLevelDestination` in `:app`, with two Material Symbols icons (outlined and filled).

### Add a component to the Catalog
1. Add its description as text (see "Add or change text"): key `catalog_entry_my_thing`.
2. Add a `CatalogEntry("my_thing", "UriMyThing", R.string.catalog_entry_my_thing)` to the right section in `feature-catalog/.../catalog/CatalogEntries.kt`.
3. Add `"my_thing" -> MyThingDemo()` to `ComponentDemo` in `screen/demo/ComponentDemo.kt`.
4. Write `MyThingDemo()` in the matching `*Demos.kt` file, using `DemoSection(localizedString(R.string.catalog_demo_…)) { … }`.
5. **Check it in dark mode** (see "Force dark mode" below). That's how the toast and divider bugs were found.

### Add or change text
1. In the `strings.xml` of the module that shows it, add `<string name="profile_thing">profileThing</string>`. Use the module's prefix. If two or more modules need it, put it in `core-ui` as `common_thing` instead.
2. In `core/src/main/assets/localization/translations.json`, add `"profileThing": "…"` to **every** language block.
3. In the screen: `localizedString(R.string.profile_thing)`. In a file that also uses `core-ui` keys, import those as `com.septianbeneran.urithiru.core.ui.R as CoreUiR`.
4. Run `./gradlew :core:testDevDebugUnitTest`. It fails if a key is missing in any language, so you can't forget one.
- To only fix wording, edit the JSON value. No Kotlin changes needed.

### Add a language
1. Copy the `"en"` block in `translations.json`, name it with the language code (`"ms"`, `"pt-BR"`), and translate every value. Keep `{0}` placeholders exactly as they are.
2. Set `"languageName"` to the language's own name (`"Bahasa Melayu"`). That's what the picker shows.
3. Bump `"version"` at the top.
4. Run the test from the recipe above. The new language appears in the picker automatically.

### Add an icon
1. Go to fonts.google.com/icons, pick the icon, and choose **Outlined** (and **Filled** too if it's for a tab).
2. Download the **SVG**.
3. In Android Studio: right-click `core-ui/src/main/res/drawable` → **New → Vector Asset** → **Local file**.
4. **Naming:** `name.xml` for outlined and `name_filled.xml` for filled (e.g. `person.xml`, `person_filled.xml`).
5. Tint it with `MaterialTheme.colorScheme`, never a fixed color.
6. **Never draw icons by hand.** Only official Material Symbols.

### Add an API endpoint
1. Add the DTO (with `mapToEntity()`), the Retrofit function with `@Url`, the data source, the repository, the use case, and bind them in `di/`.
2. Write the endpoint path in the data source's call, relative to the base URL (e.g. `api.getProducts("products", …)`).
3. `README.md` has the full example, and `api-dummyjson` is the cleanest one to copy.

### Change the onboarding text
- It's in your JSONBin bin `6ae0e204ac6210605a56e0bf`; the ID is `ONBOARDING_BIN_ID` in `SplashScreenViewModel`.
- The bin must stay **public**. Check it in a browser: `https://api.jsonbin.io/v3/b/6ae0e204ac6210605a56e0bf`.
- The text is only downloaded on first launch, so clear the app data to see a change.
- **JSONBin text is shown as-is, not translated.** Pages the bin doesn't provide use the translated defaults (`onboarding…` keys in `translations.json`). To keep onboarding translated, either leave those pages out of the bin, or put translation keys in the bin instead of sentences.

### Force dark mode / see first launch again
```
adb shell cmd uimode night yes      # dark (no = light)
adb shell pm clear com.septianbeneran.urithiru.dev   # first launch again
```

---

## 6. Known issues and quick wins

Small tasks, good for a lunch break:
- **`UriMediaPlaceHolder` is light blue**, which clashes with gray cards. Make it neutral (`surfaceVariant`).
- **The manifest still asks for** camera, contacts and location, and nothing uses them anymore. Remove them before any Play upload.
- **Login tokens are unencrypted** in DataStore. That's fine for the demo account; a real app should encrypt them.
- **A failed image doesn't retry** until it scrolls off screen and back. That needs a connectivity observer (section 8, step 7).
- **Repositories expose their cache** (`repository.cache`). Fix this when Room arrives.
- **The Indonesian translations were written by Claude.** Have a native speaker read them (e.g. "3,5 berat" on weapon cards, "Hadiah" for boss drops).
- **Startup waits on two disk reads.** `UrithiruProject.onCreate` reads the theme and language from DataStore with `runBlocking`, so they're ready before the first frame (like AppCompat does), and the translation JSON loads on the first `localizedString` call. Each is a few milliseconds today. If startup ever feels slow: measure first (`adb shell am start -W com.septianbeneran.urithiru.dev/com.septianbeneran.urithiru.MainActivity`, compare `TotalTime`), then move theme and language to SharedPreferences, or keep the splash on screen while they load (`installSplashScreen().setKeepOnScreenCondition`).
- **A missing translation shows its key on screen**, even in release builds. That's on purpose; the translation test catches it for app text, but not for keys sent by a backend.

---

## 7. Components still worth adding to `core-ui`

**Rule:** a component goes in `core-ui` if 2+ features use it, or if it's a basic building block. Every new one also gets a **Catalog entry**.

Already have: Avatar, Badge, Banner, Button, CheckBox, Dialog, Divider, Dot, EmptyState, ErrorState, Image, LanguagePicker, ListFooter, ListItem, Loader, MediaPlaceHolder, NavigationBar, ProgressBar, RadioButton, SearchBar, Skeleton, Stepper, TextField, ThemePicker, Toast, TopAppBar.

Next, in rough order:
1. **`UriChip` / `UriChipGroup`**: Shop categories.
2. **`UriMediaCard`**: image + title + badge + chevron, for weapons, products and favorites.
3. **`UriStatRow`**: label + value (+ bar), for weapon stats and product ratings.
4. **`UriPullToRefresh`**: wraps Material 3 `PullToRefreshBox`.
5. **`UriBottomSheet`**: filters and sorting.
6. **`UriTabRow`**: top tabs inside a feature (never a second bottom bar).
7. **`UriSnackbar` vs `UriToast`**: pick one way to show short messages.

**Design tokens** (postponed): `UriSpacing`, `UriShapes` and `UriElevation` via `CompositionLocal`, so screens use `UrithiruTheme.spacing.md` instead of `16.dp`.

---

## 8. Error handling: what's done, what's left

✅ **Done:**
- **1.** Typed `AppError`.
- **2.** Errors are mapped once, in `BaseDataSource`, and `CancellationException` is rethrown.
- **3.** `AppError.toMessage()` gives a string resource per error.
- **4.** The right UI for each case: full-screen `UriErrorState`, a footer when the next page fails, an inline login error.
- **6.** Token refresh: `TokenAuthenticator` in `api-dummyjson` refreshes on a 401 and retries once.
- **Backend error text is ready to translate:** `localizedString(text)` translates a key and shows plain text as-is, so a backend can send either in `errorTitle` / `errorDescription`. Still to do: parse those fields into `AppError` once a real backend exists (DummyJSON only sends `{"message": …}`).

⏳ **Left:**
- **5. Automatic retry:** `retryWhen` with backoff (1s, 2s, 4s), **only** for `NoInternet` and `Timeout`. Never retry 4xx errors.
- **7. Offline awareness:** a `ConnectivityObserver` (`Flow<Boolean>`) and an "You're offline" `UriBanner`, plus showing cached data marked as stale instead of an error.
- **8. Crash reporting:** Timber (debug builds only) plus Crashlytics or Sentry, and a `CoroutineExceptionHandler` on `@ApplicationScope`.
- **Blocking errors** (e.g. session expired): `BaseScreenUiState` has `showErrorDialog` and `errorMessage`, but nothing uses them. Wire them into `BaseScreen` with `UriDialog`, or delete them.

---

## 9. App launcher icon (not done)
1. Design it at 108×108dp, keeping the important part inside the center **66dp**, because launchers crop the corners.
2. In Android Studio: right-click `app/src/main/res` → **New → Image Asset**. Add a **monochrome layer** in the same dialog so it works with Android 13+ themed icons.
3. Play Store needs a 512×512 PNG; replace `app/src/main/ic_launcher-playstore.png`.
4. No design skills? **IconKitchen** (icon.kitchen) exports adaptive icons.

---

## 10. Roadmap

**Phase 0: hygiene** ✅ done 2026-10-27

**Phase 1: foundations**
- [x] Typed `AppError` + error, empty and skeleton states
- [x] `collectApi` key, so a new search cancels the old one
- [x] Full theme and type mapping, dark mode, saved `ThemeMode`
- [ ] Design tokens (postponed)

**Phase 2: showcase shell**
- [x] Modules renamed after their content (`feature-explore`, `api-eldenring`, …)
- [x] Live onboarding + DummyJSON login + first-launch-only
- [x] 4-tab bar / rail (Catalog · Shop · Explore · Profile)
- [x] Catalog tab with interactive demos
- [ ] Shop tab (section 3)
- [x] Profile tab + token refresh + logout
- [ ] App launcher icon (section 9)

**Phase 3: depth**
- [ ] Room + offline cart / favorites
- [ ] `UriChip`, `UriMediaCard`, `UriStatRow`
- [ ] Explore as a mixed compendium feed (bosses, weapons, armor, items, locations, NPCs)
  - [x] 1. Housekeeping: `Boss` package fixed, `ApiDto.total`, endpoint paths for every section
  - [x] 2. Compendium data layer (`CompendiumKind`, sealed `CompendiumEntry`, `CompendiumPage`) with Bosses end to end
  - [x] 3. Armor, Items, Locations, NPCs
  - [x] 4. Move Weapons into the compendium classes
  - [x] 5. Feed use case that mixes sections, with unit tests (`GetCompendiumFeedPageUseCase`)
  - [x] 6. Explore screen: real section chips, mixed feed, card styles per type
  - [x] 7. Search across every section, grouped by type
  - [x] 8. One shared detail screen
  - [ ] 9. Polish
    - [x] Pull-to-refresh, and failed sections retried on the next load
    - [x] `ExploreViewModel` unit tests
    - [ ] Cache the first feed page (repository emits cache, then network). Deferred for now.
    - [x] Region rows on All ("Around Limgrave"), built from what the feed has loaded
- [ ] Connectivity observer + offline banner
- [x] All UI text in one translation JSON (English + Bahasa Indonesia), read with `localizedString`
- [x] Language picker in onboarding and Profile; English by default, never the device language
- [ ] Download a newer translation file (at splash, applied on the next launch)
- [ ] Parse backend `errorTitle` / `errorDescription` into `AppError`

**Phase 4: ship it**
- [ ] Remove the unused permissions from the manifest
- [ ] Release build with R8 (`isMinifyEnabled`) + keep rules for Retrofit and serialization
- [ ] Unit tests for repositories and ViewModels, plus CI (GitHub Actions)
- [ ] Tag a `template-v1` release, then Play Console internal testing

**On hold**
- [ ] Publish `buildlogic` and `navigation-processor` as private libraries (e.g. GitHub Packages), so building needs your permission. Put on hold 2026-11-03: too much overhead for one person and one app, and a private repo is enough for now. Before picking it up: read the version catalog at runtime instead of through `LibrariesForLibs`, make the processor's package names configurable, give the plugins namespaced IDs, and add tests (KSP compile-testing for the processor, Gradle TestKit for the conventions).

---

## 11. Debugging tips learned the hard way

- **The emulator rolls back on restart.** It's started with `-no-snapshot-save`, so a restart can bring back an old build. Reinstall (`./gradlew :app:installDevDebug`) after every restart.
- **Don't touch the emulator window while a test script runs.** Mouse clicks there go straight into the app.
- **To see an animation frame by frame:**
  ```
  adb shell screenrecord --time-limit 4 /sdcard/clip.mp4
  adb pull /sdcard/clip.mp4
  ffmpeg -i clip.mp4 -vf "fps=20,scale=270:-1,tile=8x3" -frames:v 1 sheet.png
  ```
  `ffmpeg` is installed through WinGet.
- **`uiautomator dump` can lag one screen behind** on a slow emulator. Trust screenshots over text dumps.
- **To see the back stack,** log `navController.currentBackStack.value` in `NavHostChamber`. That's how the "tab pops home away" bug was found.
- **To pretend the phone is in another language** (the app should still start in English):
  ```
  adb shell cmd locale set-app-locales com.septianbeneran.urithiru.dev --locales id-ID
  adb shell cmd locale set-app-locales com.septianbeneran.urithiru.dev --locales ""   # undo
  ```
- **Clean screenshots** (9:41, full battery, no notifications) use demo mode:
  ```
  adb shell settings put global sysui_demo_allowed 1
  adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0941
  adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
  adb shell am broadcast -a com.android.systemui.demo -e command exit   # undo
  ```
  Screenshots in `docs/screenshots` are scaled to 432×960.

---

## 12. Working from the office without AI

- `git pull` first, and use one branch per task (`feature/shop-list`, `fix/stepper-colors`).
- Small commits with clear messages.
- Write questions and blockers under "Open questions" below and commit them. They're the to-do list for the next session.
- If `./gradlew` says "permission denied": `sh gradlew …`, or `gradlew.bat` on Windows.

## Open questions
- (add yours here)

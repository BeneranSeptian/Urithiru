# For Reading: What To Do Next

Notes for working on Urithiru away from the main laptop. Read this on your phone, pick something, and do it at the office (no AI needed). For how the project is built, see `SUMMARY.md`.

---

## 1. Build a real demo app

The template is strong, but a template only convinces people once it ships something. Pick **one** app that makes every part of the architecture do real work.

### ⭐ Recommended: "Tarnished Companion", an Elden Ring compendium
You already have the API, the entities and two features. Extend them into:
- **Tabs:** Home · Weapons · Bosses · Favorites
- **Weapons:** search, filter by category (chips), infinite scroll, and a detail screen with attack/defence/scaling stats
- **Bosses:** list and detail (`BOSSES_V1` is already in `api-b`)
- **Favorites:** save weapons and bosses offline (this is a good reason to add Room)
- **Build compare:** pick 2 weapons and compare their stats side by side, which is a nice custom UI challenge

Why this one: it uses lists, details, paging, search, caching, offline mode, bottom navigation and the error states, and the API needs no key.

### Second feature: a game discovery screen using IGDB
`@IgdbNetwork` and the Twitch token code already exist but aren't used. IGDB requires a Twitch app token, so this feature finally gives that code a job:
- "Games like Elden Ring" or a trending games list, with cover art and screenshots
- It shows **auth + token refresh**, which is the most impressive part of a template
- You'll need a free Twitch developer app (dev.twitch.tv/console) to get a client ID and secret. Keep them in `local.properties`, never in git.

### Other ideas (if Elden Ring gets boring)
- **Pokédex** (PokéAPI): classic, no key, huge dataset, good for paging
- **Movie watchlist** (TMDB): free key, images, good for favorites and Room
- **Expense tracker**: offline-first, no API, so it tests the local/Room side

Tip: compare your structure with Google's **Now in Android** app (github.com/android/nowinandroid). It uses the same ideas (modules, convention plugins, an offline-first repository) and is a good reference when you're unsure.

---

## 2. Icons

### App launcher icon
1. **Design it** at 108×108dp, keeping the important part inside the center **66dp** safe zone, because launchers crop the edges into circles or squircles. Figma (free) works well.
2. **Generate it** in Android Studio: right-click `app/src/main/res` → **New → Image Asset**. It creates the adaptive icon (foreground + background) for every density.
3. **Add a monochrome layer** in the same dialog, so the icon works with Android 13+ **themed icons**.
4. **Play Store** needs a separate 512×512 PNG. `app/src/main/ic_launcher-playstore.png` is already there; replace it.
5. No design skills? Try **IconKitchen** (icon.kitchen), a free web generator that exports Android adaptive icons.

### In-app icons
- **Material Symbols** (fonts.google.com/icons): download the **SVG**, then right-click `core-ui/src/main/res/drawable` → **New → Vector Asset** → *Local file*. That's how your current drawables were made, so keep doing it.
- Avoid the `material-icons-extended` dependency. It's large and no longer updated; import only the icons you use.
- Other free sets: **Phosphor**, **Tabler**, **Lucide** (MIT/ISC licences, fine for apps).
- **Game-themed icons** (swords, shields, skulls): **game-icons.net**. These are CC BY 3.0, so you **must credit the authors**; add an "About / Credits" screen.
- **Naming:** use `ic_<name>_<style>`, for example `ic_sword_outlined.xml` or `ic_heart_filled.xml`. The current drawables (`close.xml`, `info.xml`) have no prefix, so rename them while the set is still small.
- Draw them on a **24dp** grid and tint them with `MaterialTheme.colorScheme` instead of hardcoded colors, so they work in dark mode.

---

## 3. Components to add to `core-ui`

You already have: Avatar, Badge, Banner, Button, CheckBox, Dialog, Divider, Dot, ListItem, Loader, MediaPlaceHolder, ProgressBar, RadioButton, Stepper, Toast, TopAppBar.

**Rule:** a component belongs in `core-ui` if 2+ features use it, or if it's a basic building block. Otherwise keep it in the feature.

### Most useful next (in order)
1. **`UriErrorState`**: icon + message + "Retry" button for full-screen failures. This pairs with section 4.
2. **`UriEmptyState`**: "No weapons found" with an illustration.
3. **`UriSkeleton` / shimmer**: placeholder rows while loading, which feels faster than a spinner.
4. **`UriTextField` / `UriSearchBar`**: `WeaponList` uses a raw `OutlinedTextField` today.
5. **`UriImage`**: wraps Coil's `AsyncImage` with your placeholder and error image, so every image loads the same way.
6. **`UriChip` / `UriChipGroup`**: filters (weapon category, boss region).
7. **`UriNavigationBar` styling**: for the bottom bar or rail (see section 5).
8. **`UriTabRow`**: top tabs for sub-sections inside a feature.
9. **`UriBottomSheet`**: filters, sorting, quick actions.
10. **`UriPullToRefresh`**: wraps Material 3 `PullToRefreshBox`.
11. **`UriSnackbar`**: decide how it relates to `UriToast`, and keep just one way to show short messages.

### Design tokens (do this before adding many components)
Add `UriSpacing` (4, 8, 12, 16, 24, 32), `UriShapes` and `UriElevation`, provided through `CompositionLocal` in `UrithiruTheme`. Then components use `UrithiruTheme.spacing.md` instead of `16.dp`, and changing the look later is a one-file edit.

---

## 4. Make error handling more powerful

**Today:** every failure becomes `ErrorResponse(code, message)`, any exception gets the code `6969`, and the UI mostly shows a "Retry" button. Here's a step-by-step upgrade.

### Step 1: typed errors (in `:core`)
```kotlin
sealed interface AppError {
    data object NoInternet : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError                 // 401
    data object NotFound : AppError                     // 404
    data class Server(val code: Int) : AppError         // 5xx
    data class Http(val code: Int, val message: String?) : AppError
    data class Parse(val cause: Throwable) : AppError
    data class Unknown(val cause: Throwable) : AppError
}
```
Make `ApiResult.Error` carry `AppError`, and delete the `6969` code.

### Step 2: map errors in one place (`BaseDataSource.getResult`)
```kotlin
catch (e: UnknownHostException)   -> AppError.NoInternet
catch (e: SocketTimeoutException) -> AppError.Timeout
catch (e: SerializationException) -> AppError.Parse(e)   // or JsonSyntaxException while on Gson
catch (e: IOException)            -> AppError.NoInternet
catch (e: Exception)              -> AppError.Unknown(e)
// non-2xx: map by response.code(), and read errorBody() for the server's message
```
Catch `CancellationException` separately and rethrow it, or coroutine cancellation will break.

### Step 3: user-friendly messages (in `:core-ui`)
`fun AppError.toMessage(): Int` returns a string resource (for example "You're offline", "Server is having trouble"). Using resources means you can translate later.

### Step 4: show errors in the right way
- **First load fails:** full-screen `UriErrorState` with Retry
- **Pagination or refresh fails:** keep the existing list and show a toast/snackbar ("Couldn't load more")
- **A user action fails** (for example saving a favorite): a toast
- **Something blocking** (session expired): `UriDialog`. `BaseScreenUiState` already has `showErrorDialog` and `errorMessage`; wire them into `BaseScreen`.

### Step 5: automatic retry, but only when it makes sense
Use `retryWhen` in `resultFlow` with exponential backoff (1s, 2s, 4s) **only** for `NoInternet` and `Timeout`. Never retry 4xx errors.

### Step 6: token refresh with an OkHttp `Authenticator`
On a 401, the authenticator calls `RefreshTokenInterface` (the Twitch implementation already exists), saves the new token, and retries the request once. Features never see the expired-token case. This is the IGDB groundwork from section 1.

### Step 7: offline awareness
Add a `ConnectivityObserver` in `:core` (`ConnectivityManager.NetworkCallback` exposed as `Flow<Boolean>`), and show a `UriBanner` "You're offline" at the top of the app. Pair it with **cache fallback**: when the network fails, the repository emits the cached list with an `isStale = true` flag instead of an error.

### Step 8: know when things break in the wild
- **Timber** for logging (debug builds only)
- **Firebase Crashlytics** or **Sentry** for crashes; also send `AppError.Unknown` as a *non-fatal* report
- A `CoroutineExceptionHandler` on the `@ApplicationScope` scope, so background save errors don't vanish silently

---

## 5. Bottom navigation bar / rail (decided)

- **Where:** in `:app`, wrapping the single `NavHost` in `NavHostChamber`. Features don't know the bar exists.
- **What:** `NavigationSuiteScaffold` (artifact `androidx.compose.material3:material3-adaptive-navigation-suite`). It's a bottom bar on phones and a rail on tablets and landscape, so you get both for free.
- **Tabs:** a hand-written `TopLevelDestination` enum in `:app` that points at the feature graph objects (`FeatureBNavGraph`, `FeatureANavGraph`, …).
- **Hide/show:** the bar shows only when the current destination is inside a tab graph, so it hides itself on splash and onboarding (`destination.hierarchy.any { it.hasRoute(graph::class) }`).
- **Switching tabs:** add `Navigator.navigateToTopLevel(graph)` with `popUpTo(start) { saveState = true }`, `launchSingleTop = true` and `restoreState = true`, so each tab keeps its own history.
- **Watch out:** `BaseScreen` applies `systemBarsPadding()`, which pads twice inside the scaffold. Fix the insets.
- **A new feature that's a peer of Home** gets one more line in `TopLevelDestination`.
- **A feature with internal sections** (for example Shop → Browse/Cart/Orders) uses **top tabs** inside the feature. Never stack two bottom bars. Give a feature its own bottom bar only if it's a full "mode switch", and hide the main bar there.

---

## 6. Suggested roadmap

**Phase 0: hygiene (small, good for an office lunch break)**
- [ ] Add `testImplementation(junit)` + AndroidX test dependencies to the convention plugins (library test stubs don't compile today)
- [ ] Add `LogcatInterceptor` only when `BuildConfig.DEBUG` is true
- [ ] Remove `println("masuk observer")` from `PersonalizeExperienceScreen`
- [ ] Rename `postRereshToken` → `postRefreshToken`
- [ ] Update the README (`graph =` not `navGraph =`, module list, Gradle version)

**Phase 1: foundations**
- [ ] Typed `AppError` + `UriErrorState` / `UriEmptyState` / skeleton (sections 3 and 4)
- [ ] Fix `collectApi` so a new search cancels the previous request
- [ ] Design tokens (spacing and shapes)

**Phase 2: demo app shell**
- [ ] Bottom bar / rail (section 5)
- [ ] Room + Favorites tab
- [ ] App icon + themed icon (section 2)

**Phase 3: content**
- [ ] Bosses list and detail
- [ ] Weapon compare screen
- [ ] IGDB feature with token refresh

**Phase 4: ship it**
- [ ] Release build type with R8 (`isMinifyEnabled`) + keep rules
- [ ] Dark theme check
- [ ] Unit tests for repositories and ViewModels, plus CI (GitHub Actions)
- [ ] Upload to Play Console internal testing, and put the link in the README

---

## 7. Working from the office without AI

- `git pull` first, and work on a branch per task (`feature/bottom-nav`, `fix/error-handling`).
- Small commits with clear messages, so it's easy to review later at home.
- Write down questions and blockers at the bottom of this file under "Open questions" and commit them. They're the to-do list for the next session at home.
- If `./gradlew` says "permission denied", run `sh gradlew …` or `chmod +x gradlew` (on Windows, use `gradlew.bat`).

## Open questions
- (add yours here)

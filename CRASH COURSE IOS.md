# Crash Course: iOS for an Android (Kotlin) Developer

A reading guide that teaches iOS by rebuilding **Urithiru** in your head: every idea is mapped to something you already wrote in this repo. Read it on your phone; no code needs to run. Companion: `CRASH COURSE BACKEND.md`.

---

## 0. Before anything: the honest setup facts

- **You need a Mac to build iOS apps.** Xcode (the only official IDE + build tool) runs only on macOS. On Windows you can still *learn Swift the language* (Swift has a Windows toolchain), but not SwiftUI or the simulator. Options: a used Mac mini, a cloud Mac (MacinCloud and similar), or a friend's Mac for weekends.
- **Running on your own iPhone** is free with an Apple ID (the install expires after 7 days).
- **Shipping to TestFlight or the App Store** needs the Apple Developer Program (USD 99/year).
- **Alternative path:** Kotlin Multiplatform (KMP) lets you share `core-entity`, `api-*` and even Compose UI (Compose Multiplatform) with iOS. That's covered in section 11. Still learn native iOS basics first, because KMP iOS apps still have an Xcode project, Swift glue and Apple-specific rules.

---

## 1. The big map: Android → iOS

| Android (Urithiru) | iOS equivalent | Notes |
|---|---|---|
| Kotlin | **Swift** | Very similar feel. Biggest differences: value types, ARC, and default `internal` visibility. |
| Jetpack Compose | **SwiftUI** | Both are declarative. The concepts map almost 1:1. |
| Android Studio + Gradle | **Xcode** + **Swift Package Manager (SPM)** | No Gradle. Projects are `.xcodeproj`. Libraries and modules are SPM packages. |
| Gradle modules (`:core`, `:api-a`, …) | **SPM targets** in a local package | Same idea: compile-time boundaries plus explicit dependencies. |
| `buildlogic` convention plugins | Nothing built in | `Package.swift` is Swift code, so you can share helpers. Big teams use **Tuist** or **XcodeGen**. |
| Product flavors (`dev/uat/beta/prod`) | **Build configurations** + **`.xcconfig` files** + **schemes** | Section 7. |
| `BuildConfig.ELDEN_RING_BASE_URL` | An `Info.plist` key filled from `.xcconfig` | Read it with `Bundle.main.object(forInfoDictionaryKey:)`. |
| Hilt | **No standard.** Initializer injection + a composition root | Libraries exist (Factory, swift-dependencies), but plain `init` injection is the norm. |
| Retrofit + OkHttp | **`URLSession`** + `async/await` | Built in. No codegen needed. |
| kotlinx-serialization | **`Codable`** (`JSONDecoder`) | Built in. `CodingKeys` play the role of `@SerialName`. |
| Coroutines `suspend` | **`async` functions** | Same idea. `await` is written explicitly at each call. |
| `Flow` | **`AsyncSequence` / `AsyncStream`** (or the older **Combine**) | SwiftUI rarely needs streams; observation handles most of it. |
| `viewModelScope.launch` | **`Task { }`** or SwiftUI's **`.task { }`** | `.task` is cancelled automatically when the view disappears. |
| `Dispatchers.Main` | **`@MainActor`** | Swift 6 checks thread safety at compile time. |
| `ViewModel` + `StateFlow` | **`@Observable` class** | Views re-render when the properties they read change. |
| `remember { mutableStateOf() }` | **`@State`** | |
| State hoisting `(value, onValueChange)` | **`@Binding`** (`$value`) | |
| `CompositionLocal` | **`@Environment`** | |
| `LaunchedEffect(key)` | **`.task(id: key) { }`** | Restarts and cancels the old task when `key` changes. |
| `LazyColumn` | **`List`** / `ScrollView` + `LazyVStack` | |
| `Column` / `Row` / `Box` | **`VStack` / `HStack` / `ZStack`** | |
| Coil `AsyncImage` | **`AsyncImage`** (built in, basic) | Use Kingfisher or Nuke for caching. |
| `@Preview` | **`#Preview { }`** | |
| Navigation Compose + KSP route generator | **`NavigationStack`** + `navigationDestination(for:)` | Type-safe routes are built in, so **no code generator is needed**. |
| DataStore | **`UserDefaults`** (small values) | Never store secrets there. Use the **Keychain**. |
| Room | **SwiftData** (or Core Data, or GRDB) | |
| `strings.xml` | **String Catalogs** (`Localizable.xcstrings`) | |
| `res/drawable` | **Asset Catalog** (`Assets.xcassets`) + **SF Symbols** | SF Symbols is Apple's free icon set, built into the OS. |
| JUnit + Turbine + MockK | **Swift Testing** (`@Test`, `#expect`) / XCTest | Fakes via protocols. Mocking libraries are rare. |
| Play Console + internal testing | **App Store Connect** + **TestFlight** | Section 10. |
| APK/AAB signing keystore | **Certificates + provisioning profiles** | Xcode's "Automatically manage signing" handles most of it. |

---

## 2. Swift for Kotlin developers

### 2.1 Same ideas, different spelling

```swift
let name = "Moonveil"            // val
var page = 0                     // var
let image: String? = nil         // nullable is the same: String?
let length = image?.count ?? 0   // ?. is the same; ?? is Kotlin's ?:

// Kotlin smart-cast / ?.let  →  if let / guard let
if let image {                   // image is non-optional inside this block
    print(image)
}

func load(id: String?) {
    guard let id else { return } // "early return unless non-null"
    print(id)                    // id is non-optional from here on
}

// Functions: parameters have *argument labels*
func weapons(name: String?, page: Int, limit: Int = 20) -> [Weapon] { [] }
weapons(name: nil, page: 0)      // labels are required at the call site

// Use _ to drop the label
func greet(_ who: String) {}
greet("Tarnished")

// Lambdas → closures. Trailing closures work just like in Kotlin.
let names = weapons.map { $0.name }           // $0 is Kotlin's `it`
let heavy = weapons.filter { w in w.weight > 10 }
```

### 2.2 `when` → `switch` (exhaustive, no fall-through)

```swift
switch state {
case .idle: print("idle")
case .loading: print("loading")
case .loaded(let weapons): print(weapons.count)
case .failed(let error) where error.isOffline: print("offline")
case .failed: print("failed")
}
```

### 2.3 sealed interface → `enum` with associated values

Urithiru's `BaseState` and `ApiResult` become one small enum:

```swift
// Kotlin: sealed class BaseState<out T> { StateInitial, StateLoading, StateSuccess(value), StateFailed(error) }
enum Loadable<Value> {
    case initial
    case loading
    case success(Value)
    case failure(Error)
}
```

### 2.4 interface → `protocol`, extension functions → `extension`

```swift
protocol WeaponRepository {
    func weapons(name: String?, page: Int, limit: Int) async throws -> [Weapon]
}

extension String {
    var isBlank: Bool { trimmingCharacters(in: .whitespaces).isEmpty }
}
```

Protocols can also have default implementations (in an `extension`) and associated types (similar to generics on an interface).

### 2.5 ⚠️ The biggest mental shift: `struct` vs `class`

- **`struct` is a value type.** Assigning or passing it **copies** it. A `let` struct is deeply immutable. Most of your models (`Weapon`, `Attributes`, UI state) should be structs. They behave like Kotlin `data class` but are safer, because nobody can mutate your copy.
- **`class` is a reference type**, like every Kotlin class. Use it for things with identity and shared mutable state: view models, repositories, caches.
- Mutating a struct's own properties from a method needs `mutating func`.
- Swift arrays and dictionaries are structs too. `var a = b` copies them (efficiently, copy-on-write).

### 2.6 ⚠️ Memory: ARC, not a garbage collector

Swift counts references, so **retain cycles leak**: A holds B, and B holds A. The classic case is a closure stored on an object that captures `self`:

```swift
final class Player {
    var onFinish: (() -> Void)?
    func start() {
        onFinish = { [weak self] in      // without [weak self] this is a leak
            self?.cleanup()
        }
    }
    func cleanup() {}
}
```

In SwiftUI plus `async/await` code you hit this much less often, but you must know it exists. `weak` means a nullable reference that doesn't keep the object alive.

### 2.7 Errors: `throws` instead of `ApiResult.Error`

Swift functions declare `throws`, and callers must write `try`. This replaces most of `ApiResult` and `getResult { }`:

```swift
enum ApiError: Error {
    case noInternet
    case timeout
    case http(code: Int)
    case decoding(Error)
    case unknown(Error)
}

do {
    let weapons = try await repository.weapons(name: nil, page: 0, limit: 20)
} catch ApiError.noInternet {
    showOfflineBanner()
} catch {
    show(error)                                  // `error` is implicit here
}

let maybe = try? await repository.weapons(name: nil, page: 0, limit: 20)  // nil on failure
```

Notice that this is exactly the typed `AppError` idea from `FOR READING.md`, section 4. Swift pushes you toward it by default.

### 2.8 Visibility: the default is different!

| | Kotlin | Swift |
|---|---|---|
| default | `public` | **`internal`** (visible inside the module only) |
| module-only | `internal` | `internal` |
| file-only | `private` (top level) | `fileprivate` |
| type-only | `private` | `private` |

Consequence: in a multi-module app, **everything a feature uses from `Core` must be marked `public`**. That includes a struct's `init`, because the auto-generated memberwise initializer is `internal`. This is the #1 confusing compile error when you split into modules.

### 2.9 Concurrency (coroutines → async/await)

| Kotlin | Swift |
|---|---|
| `suspend fun f()` | `func f() async` |
| `launch { }` | `Task { }` |
| `async { } / await()` | `async let x = f()` then `await x` |
| `coroutineScope { }` + many `async` | `withTaskGroup` / `withThrowingTaskGroup` |
| `job.cancel()` | `task.cancel()` |
| `withContext(Dispatchers.Main)` | mark the function or type `@MainActor` |
| `Mutex` / confined state | **`actor`**: a class whose state can only be touched by one task at a time |
| `CancellationException` | `CancellationError`. Check with `try Task.checkCancellation()` |
| `delay(300)` | `try await Task.sleep(for: .milliseconds(300))` |

**Swift 6 strict concurrency:** the compiler refuses code that might share mutable state across threads. Types passed between tasks must be **`Sendable`**. Structs of `Sendable` fields are automatically `Sendable`, and so are `actor`s. Classes need `final` + immutable `let`s, or `@MainActor`. Early on, mark your view models `@MainActor` and keep models as structs; that removes 90% of the errors.

---

## 3. SwiftUI for Compose developers

### 3.1 A screen

```swift
import SwiftUI

struct WeaponRow: View {                 // a View is a struct, not a function
    let weapon: Weapon                   // inputs = stored properties

    var body: some View {                // body = your @Composable content
        HStack(spacing: 16) {            // Row(horizontalArrangement = spacedBy(16.dp))
            AsyncImage(url: URL(string: weapon.image ?? "")) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                Color.gray.opacity(0.2)
            }
            .frame(width: 64, height: 64)            // Modifier.size(64.dp)
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading) {            // Column
                Text(weapon.name)
                    .font(.headline)
                    .lineLimit(1)
                Text(weapon.category)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
        }
    }
}

#Preview {
    WeaponRow(weapon: .preview)
}
```

- **Modifiers are chained after the view**, and **their order matters**, exactly like `Modifier` chains. `.padding().background(.red)` is not the same as `.background(.red).padding()`.
- Units are **points**, which are roughly dp. There's no `.dp` suffix.
- `some View` means "some concrete view type I won't name". Treat it as boilerplate.

### 3.2 State, mapped

| Compose | SwiftUI |
|---|---|
| `var text by remember { mutableStateOf("") }` | `@State private var text = ""` |
| pass `text` + `onTextChange` down | pass `$text` (a `Binding<String>`) and declare `@Binding var text: String` in the child |
| `hiltViewModel()` + `collectAsStateWithLifecycle()` | `@State private var viewModel = WeaponListViewModel(...)`, which observes automatically |
| `CompositionLocalProvider(LocalX provides x)` | `.environment(x)` and read it with `@Environment(X.self) var x` |
| `LaunchedEffect(Unit) { }` | `.task { }` |
| `LaunchedEffect(query) { }` | `.task(id: query) { }` |
| `DisposableEffect` | `.onAppear { }` / `.onDisappear { }` |
| `Scaffold(topBar = ...)` | `NavigationStack` + `.navigationTitle(...)` + `.toolbar { }` |
| `AlertDialog` | `.alert(...)` |
| `ModalBottomSheet` | `.sheet(...)` / `.presentationDetents([.medium])` |
| Material 3 | Apple's Human Interface Guidelines. Don't make iOS look like Material. |

### 3.3 The view model: `@Observable`

```swift
import Observation

@MainActor
@Observable
final class CounterViewModel {
    var count = 0                       // a public, observable property; no MutableStateFlow/asStateFlow pair
    func increment() { count += 1 }
}
```

SwiftUI tracks **which properties each `body` reads** and re-renders only those views. There's no `uiState` data class to `copy(...)`: you mutate properties directly. You *can* keep a single `UiState` struct if you like the MVI style; both work.

---

## 4. Urithiru's architecture, rebuilt in iOS

### 4.1 Modules: a local Swift package

In Xcode you'd have one **App target**, which is the `:app` module, and a local **SPM package** containing the other modules as targets:

```swift
// UrithiruKit/Package.swift
// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "UrithiruKit",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureA", targets: ["FeatureA"]),
        .library(name: "FeatureB", targets: ["FeatureB"]),
    ],
    targets: [
        .target(name: "Core"),
        .target(name: "CoreEntity"),
        .target(name: "CoreUI", dependencies: ["Core"]),
        .target(name: "CoreNavigation", dependencies: ["CoreEntity"]),
        .target(name: "ApiA", dependencies: ["Core", "CoreEntity"]),
        .target(name: "FeatureA", dependencies: ["ApiA", "CoreUI", "CoreNavigation"]),
        .testTarget(name: "ApiATests", dependencies: ["ApiA"]),
    ]
)
```

Folder layout follows the targets: `Sources/Core/...`, `Sources/ApiA/...`, `Tests/ApiATests/...`.

- This replaces `settings.gradle.kts` + `moduleImplementation(projects.xxx)`. A misspelled target name fails at resolve time, much like type-safe accessors.
- The **dependency direction stays the same**: `App → Feature* → Api* → Core, CoreEntity`, and features never import each other.
- Remote libraries go in `dependencies: [.package(url: "...", from: "1.0.0")]`. That's your `libs.versions.toml`, except versions live in `Package.swift` and are locked in `Package.resolved`.

### 4.2 Entities and DTOs: `Codable`

```swift
// CoreEntity/Weapon.swift (Kotlin: core-entity/.../Weapon.kt)
public struct Weapon: Codable, Identifiable, Hashable, Sendable {
    public let id: String
    public let name: String
    public let description: String?
    public let image: String?
    public let attack: [Attribute]
    public let defence: [Attribute]
    public let scalesWith: [Scaling]
    public let requiredAttributes: [Attribute]
    public let category: String
    public let weight: Double
}

public struct Attribute: Codable, Hashable, Sendable {
    public let name: String
    public let amount: Int
}

public struct Scaling: Codable, Hashable, Sendable {
    public let name: String?
    public let scaling: String?
}

// Core/ApiDto.swift (Kotlin: ApiDto<T>(success, count, data))
public struct ApiDto<T: Decodable>: Decodable {
    public let success: Bool?
    public let count: Int?
    public let data: T
}
```

- `Codable` = `Encodable & Decodable`. The compiler generates the JSON code, like the kotlinx-serialization plugin does.
- Optional fields (`String?`) decode as `nil` when the key is missing, like `explicitNulls = false`. **Unknown keys are ignored by default**, so you don't need an `ignoreUnknownKeys` setting.
- Renaming a field (Kotlin `@SerialName("access_token")`):

```swift
struct TwitchOAuthTokenResponse: Decodable {
    let accessToken: String
    let expiresIn: Int
    let tokenType: String

    enum CodingKeys: String, CodingKey {
        case accessToken = "access_token"
        case expiresIn = "expires_in"
        case tokenType = "token_type"
    }
}
// Or skip CodingKeys entirely:  decoder.keyDecodingStrategy = .convertFromSnakeCase
```

- **Do you need separate DTOs and entities?** Urithiru has `WeaponResponse.mapToWeapon()`. Many iOS codebases decode straight into the model when the shapes match. Keep DTOs when the API shape is ugly or unstable. Same trade-off as on Android.

### 4.3 Networking: `URLSession` replaces Retrofit + `BaseDataSource`

```swift
// Core/ApiClient.swift  (Kotlin: NetworkModule + BaseDataSource.getResult)
public struct ApiClient: Sendable {
    let baseURL: URL
    let session: URLSession

    public init(baseURL: URL, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.session = session
    }

    public func get<T: Decodable>(_ path: String, query: [URLQueryItem] = []) async throws -> T {
        var components = URLComponents(url: baseURL.appending(path: path), resolvingAgainstBaseURL: false)!
        if !query.isEmpty { components.queryItems = query }

        let data: Data
        let response: URLResponse
        do {
            (data, response) = try await session.data(from: components.url!)
        } catch let error as URLError where error.code == .notConnectedToInternet {
            throw ApiError.noInternet
        } catch let error as URLError where error.code == .timedOut {
            throw ApiError.timeout
        }

        guard let http = response as? HTTPURLResponse else { throw ApiError.http(code: -1) }
        guard (200..<300).contains(http.statusCode) else { throw ApiError.http(code: http.statusCode) }

        do {
            return try JSONDecoder().decode(T.self, from: data)
        } catch {
            throw ApiError.decoding(error)
        }
    }
}
```

Compare with Urithiru:
- `@GET` + `@Url` + `@Query` → building a `URLComponents` with `URLQueryItem`s.
- `Response<T>` + `isSuccessful` → checking `HTTPURLResponse.statusCode`.
- The magic `6969` code → a real `ApiError` case.
- `LogcatInterceptor` → there's no interceptor chain. Wrap `ApiClient` (a decorator) or use `URLProtocol`. Many teams use the Pulse library for network logging.
- `Authorization` header → build a `URLRequest`, set `request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")`, then call `session.data(for: request)`.

### 4.4 Repository and use case

```swift
// ApiA/WeaponRepository.swift
public protocol WeaponRepository: Sendable {
    func weapons(name: String?, page: Int, limit: Int) async throws -> [Weapon]
    func weapon(id: String) async throws -> Weapon
}

public final class WeaponRepositoryImpl: WeaponRepository {
    private let api: ApiClient
    private let cache: WeaponCache

    public init(api: ApiClient, cache: WeaponCache) {
        self.api = api
        self.cache = cache
    }

    public func weapons(name: String?, page: Int, limit: Int) async throws -> [Weapon] {
        var query = [URLQueryItem(name: "page", value: "\(page)"),
                     URLQueryItem(name: "limit", value: "\(limit)")]
        if let name { query.append(URLQueryItem(name: "name", value: name)) }

        let dto: ApiDto<[Weapon]> = try await api.get("weapons", query: query)
        await cache.save(dto.data)              // Kotlin: mapToEntity(saveResult = ...)
        return dto.data
    }

    public func weapon(id: String) async throws -> Weapon {
        let dto: ApiDto<Weapon> = try await api.get("weapons/\(id)")
        return dto.data
    }
}
```

- `resultFlow` (emit `Loading`, then the result) **disappears**. The caller sets `isLoading = true`, awaits, then sets the result. Loading is UI state, not data-layer state.
- **Use cases:** most iOS teams skip one-method use case classes and call the repository from the view model. Keep them if a use case combines several repositories or holds real business rules. If you want to keep Urithiru's style:

```swift
public struct GetWeaponListUseCase: Sendable {
    let repository: WeaponRepository
    public func callAsFunction(name: String?, page: Int) async throws -> [Weapon] {
        try await repository.weapons(name: name, page: page, limit: 20)
    }
}
// usage: try await getWeaponList(name: nil, page: 0)   ← like Kotlin's operator fun invoke
```

### 4.5 Local cache: replacing `BaseDataStore`

```swift
// Small values / small lists: UserDefaults + Codable (what WeaponCacheImpl does with DataStore)
public actor WeaponCache {
    private let defaults = UserDefaults.standard
    private let key = "WEAPON_LIST"

    public init() {}

    public func save(_ weapons: [Weapon]) {
        if let data = try? JSONEncoder().encode(weapons) {
            defaults.set(data, forKey: key)
        }
    }

    public func load() -> [Weapon] {
        guard let data = defaults.data(forKey: key) else { return [] }
        return (try? JSONDecoder().decode([Weapon].self, from: data)) ?? []
    }
}
```

- `actor` makes it thread-safe (like confining it to one dispatcher), and callers must `await`.
- **Secrets and tokens → the Keychain**, never `UserDefaults`. That's like Android's EncryptedSharedPreferences/Keystore.
- **Favorites, queries, offline-first → SwiftData**: an `@Model` class plus `@Query` in views. It's the Room equivalent, and the same advice as `FOR READING.md` applies: don't store big lists as JSON blobs.

### 4.6 The view model (rebuilding `WeaponListViewModel`)

```swift
// FeatureA/WeaponListViewModel.swift
import Observation

@MainActor
@Observable
final class WeaponListViewModel {
    // The UI state (Kotlin: WeaponListScreenUiState)
    var query = ""
    private(set) var weapons: [Weapon] = []
    private(set) var isLoading = false
    private(set) var error: Error?
    private var page = 0
    private var isEndReached = false
    private let pageSize = 20                // one constant, sent AND compared (fixes the "size < 20" note)

    private let repository: WeaponRepository

    init(repository: WeaponRepository) {
        self.repository = repository
    }

    // Kotlin: onAction(OnSearchButtonClick) / GetWeaponList
    func search() async {
        page = 0
        weapons = []
        isEndReached = false
        await loadNextPage()
    }

    // Kotlin: onAction(LoadNextPage)
    func loadNextPage() async {
        guard !isEndReached, !isLoading else { return }
        isLoading = true
        defer { isLoading = false }          // always runs when the function exits

        do {
            let name = query.isEmpty ? nil : query
            let newWeapons = try await repository.weapons(name: name, page: page, limit: pageSize)
            weapons += newWeapons
            page += 1
            isEndReached = newWeapons.count < pageSize
            error = nil
        } catch is CancellationError {
            // a newer search replaced this one; ignore
        } catch {
            self.error = error
        }
    }
}
```

What changed compared with `BaseViewModel.collectApi`:
- There's no `onLoading`/`onError`/`onSuccess` callback trio: plain `do/catch` around an `await`.
- There's no `Channel` of `Nonce`s for navigation. Navigation is state too: the view or a router changes the navigation path (section 5).
- The central loading dialog becomes `isLoading`, which the view displays. If you want a global overlay like `CentralLoadingDialog`, put an `@Observable` `LoadingState` in the environment.

### 4.7 The screen (rebuilding `WeaponListRoute`)

```swift
// FeatureA/WeaponListView.swift
import SwiftUI
import CoreNavigation

public struct WeaponListView: View {
    @State private var viewModel: WeaponListViewModel

    public init(repository: WeaponRepository) {
        _viewModel = State(initialValue: WeaponListViewModel(repository: repository))
    }

    public var body: some View {
        List {
            ForEach(viewModel.weapons) { weapon in
                NavigationLink(value: WeaponDetailRoute(id: weapon.id)) {   // navigation = a value
                    WeaponRow(weapon: weapon)
                }
                .onAppear {                                   // infinite scroll trigger
                    if weapon.id == viewModel.weapons.last?.id {
                        Task { await viewModel.loadNextPage() }
                    }
                }
            }
            if viewModel.isLoading && !viewModel.weapons.isEmpty {
                ProgressView().frame(maxWidth: .infinity)
            }
        }
        .overlay {
            if viewModel.weapons.isEmpty {
                if viewModel.isLoading {
                    ProgressView()
                } else if viewModel.error != nil {
                    ContentUnavailableView {
                        Label("Couldn't load weapons", systemImage: "wifi.exclamationmark")
                    } actions: {
                        Button("Retry") { Task { await viewModel.search() } }
                    }
                } else {
                    ContentUnavailableView.search(text: viewModel.query)   // built-in empty state
                }
            }
        }
        .navigationTitle("Weapons")
        .searchable(text: $viewModel.query)                  // native search bar, two-way binding
        .task(id: viewModel.query) {                         // runs on appear and whenever query changes
            try? await Task.sleep(for: .milliseconds(300))   // debounce; throws if cancelled
            guard !Task.isCancelled else { return }
            await viewModel.search()
        }
        .refreshable { await viewModel.search() }            // pull-to-refresh for free
    }
}
```

Look at `.task(id: viewModel.query)`: **when the query changes, SwiftUI cancels the previous task and starts a new one.** That's exactly the "cancel the in-flight request when a new search starts" fix listed for `collectApi` in `SUMMARY.md`, and here it comes for free.

`ContentUnavailableView` is Apple's built-in equivalent of the `UriErrorState`/`UriEmptyState` you were planning.

---

## 5. Navigation (and why you don't need the KSP processor)

Urithiru needs `@FeatureRoute`, `@FeatureGraph` and a KSP processor so that features can register screens without the app knowing them. In SwiftUI, **routes are just `Hashable` values**, and each feature registers its destinations with a modifier:

```swift
// CoreNavigation/Routes.swift  (Kotlin: core-navigation/routeparams)
public struct WeaponDetailRoute: Hashable { public let id: String; public init(id: String) { self.id = id } }
public struct BossListRoute: Hashable { public init() {} }

// CoreNavigation/Router.swift  (Kotlin: Navigator)
import SwiftUI
import Observation

@MainActor
@Observable
public final class Router {
    public var path = NavigationPath()
    public init() {}
    public func navigate(to route: some Hashable) { path.append(route) }
    public func navigateUp() { if !path.isEmpty { path.removeLast() } }
    public func popToRoot() { path = NavigationPath() }
}

// FeatureA/FeatureADestinations.swift  (Kotlin: the generated featureANavGraph)
public extension View {
    func featureADestinations(repository: WeaponRepository) -> some View {
        navigationDestination(for: WeaponDetailRoute.self) { route in
            WeaponDetailView(id: route.id, repository: repository)
        }
    }
}

// App/RootView.swift  (Kotlin: NavHostChamber + registerAllFeatureGraphs)
struct RootView: View {
    @State private var router = Router()
    let container: AppContainer

    var body: some View {
        NavigationStack(path: $router.path) {
            HomeView()
                .featureADestinations(repository: container.weaponRepository)
                .featureBDestinations(/* ... */)
        }
        .environment(router)            // any screen can do @Environment(Router.self) var router
    }
}
```

- "Pop up to, inclusive" (splash → onboarding → home) is usually **not done with the stack** on iOS. Instead the root swaps the whole view: `if hasOnboarded { MainTabs() } else { OnboardingFlow() }`. Splash screens are a launch screen plus a short state, not a route.
- **Bottom bar:** `TabView` with one `NavigationStack` per tab. Each tab keeps its own history automatically, which is what `saveState`/`restoreState` does for you on Android.
- Deep links: `.onOpenURL { url in router.navigate(to: ...) }`.

---

## 6. Dependency injection without Hilt

iOS apps usually wire everything by hand in one place, a **composition root**. That's fine because Swift has no reflection-based DI standard, and init injection is compile-time safe:

```swift
// App/AppContainer.swift  (Kotlin: all the @Module/@Binds classes)
@MainActor
final class AppContainer {
    let apiClient: ApiClient
    let weaponRepository: WeaponRepository

    init(config: AppConfig) {
        apiClient = ApiClient(baseURL: config.eldenRingBaseURL)
        weaponRepository = WeaponRepositoryImpl(api: apiClient, cache: WeaponCache())  // "singletons" = created once here
    }
}

// App/UrithiruApp.swift  (Kotlin: @HiltAndroidApp UrithiruProject + MainActivity)
@main
struct UrithiruApp: App {
    @State private var container = AppContainer(config: .current)

    var body: some Scene {
        WindowGroup {
            RootView(container: container)
        }
    }
}
```

- `@Singleton` → create the object once in the container.
- Unscoped use cases → create a new one each time (structs are cheap).
- For tests, pass a fake that conforms to the protocol: `WeaponListViewModel(repository: FakeWeaponRepository())`.
- When the container grows, look at **Factory** or Point-Free's **swift-dependencies**. Don't start with them.

---

## 7. Flavors: `dev / uat / beta / prod` on iOS

Android reads `productFlavorProperties/dev.properties` into `BuildConfig`. On iOS:

1. **Build configurations** (Project → Info → Configurations): duplicate `Debug`/`Release` into `Debug-Dev`, `Debug-Prod`, `Release-Dev`, `Release-Prod`, and so on.
2. **One `.xcconfig` per environment** (plain text key = value):

   ```
   // Dev.xcconfig
   APP_NAME = Urithiru Dev
   PRODUCT_BUNDLE_IDENTIFIER = com.septianbeneran.urithiru.dev
   ELDEN_RING_BASE_URL = https:/$()/eldenring.fanapis.com/api/
   ```
   ⚠️ `//` starts a comment in `.xcconfig`, so URLs need the `/$()/` trick shown above.
3. **Expose to code** through `Info.plist`: add a key `ELDEN_RING_BASE_URL` with the value `$(ELDEN_RING_BASE_URL)`, then read it:

   ```swift
   struct AppConfig {
       let eldenRingBaseURL: URL

       static let current = AppConfig(
           eldenRingBaseURL: URL(string: Bundle.main.object(forInfoDictionaryKey: "ELDEN_RING_BASE_URL") as! String)!
       )
   }
   ```
4. **Schemes** (`Urithiru Dev`, `Urithiru Prod`): a scheme picks which configuration Run and Archive use. Picking a scheme is your build-variant dropdown.
5. `#if DEBUG ... #endif` is your `BuildConfig.DEBUG`. Use it to add network logging only in debug.

Secrets: same rule as `CLAUDE.md`. Keep them out of git (an untracked `Secrets.xcconfig` included by the others), and remember that **anything shipped inside the app can be extracted**. Real secrets such as the Twitch client secret belong on a server (see `CRASH COURSE BACKEND.md`).

---

## 8. Design system: `core-ui` on iOS

- `UriButton`, `UriToast` and friends become SwiftUI views in a `CoreUI` target, with `public` inits.
- **Styles instead of wrapper components:** SwiftUI's idiom for "our button looks like this" is a `ButtonStyle`:

  ```swift
  public struct UriPrimaryButtonStyle: ButtonStyle {
      public init() {}
      public func makeBody(configuration: Configuration) -> some View {
          configuration.label
              .font(.headline)
              .padding(.vertical, 12)
              .frame(maxWidth: .infinity)
              .background(Color.accentColor, in: RoundedRectangle(cornerRadius: 12))
              .foregroundStyle(.white)
              .opacity(configuration.isPressed ? 0.7 : 1)
      }
  }
  // usage: Button("Search") { … }.buttonStyle(UriPrimaryButtonStyle())
  ```
- **Design tokens** (the `UriSpacing` idea from `FOR READING.md`): `public enum UriSpacing { public static let md: CGFloat = 16 }`. Colors go in the Asset Catalog with light and dark variants, then `Color("Primary", bundle: .module)`.
- **Fonts:** add the font files and list them in `Info.plist` under `UIAppFonts`, then `.font(.custom("YourFont-Bold", size: 17))`. Prefer `relativeTo: .body` so Dynamic Type (the user's text size setting) still works.
- **Icons:** SF Symbols cover most needs (`Image(systemName: "heart.fill")`). Custom icons go in the Asset Catalog as PDF or SVG with "Preserve Vector Data".
- **Toasts:** iOS has no system toast. Teams use an overlay view driven by an `@Observable` `ToastCenter` in the environment, which is your `UriToast` idea.

---

## 9. Testing

```swift
import Testing
@testable import FeatureA          // @testable lets tests see internal symbols

struct FakeWeaponRepository: WeaponRepository {
    var result: Result<[Weapon], Error>
    func weapons(name: String?, page: Int, limit: Int) async throws -> [Weapon] { try result.get() }
    func weapon(id: String) async throws -> Weapon { fatalError("not used") }
}

@MainActor
struct WeaponListViewModelTests {
    @Test func searchLoadsFirstPage() async {
        let vm = WeaponListViewModel(repository: FakeWeaponRepository(result: .success([.preview])))
        await vm.search()
        #expect(vm.weapons.count == 1)
        #expect(vm.isLoading == false)
    }

    @Test func searchStoresError() async {
        let vm = WeaponListViewModel(repository: FakeWeaponRepository(result: .failure(ApiError.noInternet)))
        await vm.search()
        #expect(vm.error != nil)
    }
}
```

- Because the code is `async/await`, you don't need a `TestDispatcherProvider` or Turbine. You just `await` the call.
- UI tests: **XCUITest** (like Espresso/Compose UI tests). Snapshot tests: Point-Free's swift-snapshot-testing.
- Run tests with ⌘U in Xcode, or `xcodebuild test -scheme "Urithiru Dev" -destination 'platform=iOS Simulator,name=iPhone 16'` in CI.

---

## 10. Shipping

1. **Bundle identifier** = `applicationId` (`com.septianbeneran.urithiru`).
2. **Signing:** in the target's *Signing & Capabilities* tab, tick "Automatically manage signing" and pick your team. Xcode creates the certificate and the provisioning profile. A profile is "this app may run on these devices / be distributed", which has no real Android equivalent.
3. **Version numbers:** *Version* (`CFBundleShortVersionString`, e.g. 1.2.0) = `versionName`. *Build* (`CFBundleVersion`) = `versionCode`, and it must increase for every upload.
4. **Archive:** Product → Archive (with a Release configuration), then Distribute → App Store Connect.
5. **TestFlight** = Play internal testing. Internal testers get builds almost immediately; external testers need a light review.
6. **App Review** is stricter than Google Play. Common rejections: crashes, broken links, missing "Sign in with Apple" when you offer other social logins, missing account deletion, vague privacy descriptions.
7. **Privacy:** fill in the *App Privacy* labels, and add usage strings (`NSCameraUsageDescription`, …) for every permission, or the app **crashes** when it asks. Your `PermissionHandler` idea maps to requesting authorization from each framework (camera, photos, location, notifications); there's no single permission API.
8. **Release optimizations:** there's no R8. Swift compiles to native code, the Release configuration uses `-O` and dead-code stripping, and there are no keep rules to write.

---

## 11. Kotlin Multiplatform: reuse Urithiru instead of rewriting

Because Urithiru is already layered, KMP fits it well:

| Urithiru module | KMP status |
|---|---|
| `core-entity` | **Easy.** Pure Kotlin + kotlinx-serialization → `commonMain`. |
| `api-*` (Retrofit) | Swap Retrofit for **Ktor Client**, which is multiplatform and has the same kotlinx-serialization setup. Use cases and repositories move to `commonMain`. |
| DataStore | DataStore has KMP support. Room also supports KMP now. |
| Hilt | Android-only. Use **Koin** or manual DI in shared code. |
| ViewModels | `androidx.lifecycle.ViewModel` is multiplatform. |
| UI | Either native SwiftUI on iOS (consumes the shared Kotlin module as an `.xcframework`), or **Compose Multiplatform** to share the screens as well. |

**When to choose KMP:** you want one codebase and you think in Kotlin.
**When to learn native:** you want iOS jobs, deep platform features, or the most "iOS-feeling" UI. Either way, sections 1–10 still matter, because even a KMP app ships through Xcode, signing and App Review.

Swift ↔ Kotlin interop gotchas (if you go KMP): Kotlin `suspend` functions show up as Swift `async`, sealed classes aren't exhaustive enums in Swift, and `Flow` needs a wrapper. The SKIE plugin by Touchlab fixes most of this.

---

## 12. Gotchas Android developers always hit

1. Things in another module are **invisible** until marked `public`, and so are their `init`s.
2. **Structs are copies.** Editing `var w = weapons[0]; w.name = …` doesn't change the array.
3. `@State` belongs to the view instance and is **only initialized once**. Passing a new value into `init` later won't reset it; use `.task(id:)` or `.onChange(of:)` to react.
4. A SwiftUI `body` is re-run often. Don't start work inside it; use `.task`.
5. **No back button logic needed.** Swipe-back and the back arrow come free with `NavigationStack`. `BackHandler` has no direct equivalent, and you rarely want one.
6. Keyboard avoidance is automatic in SwiftUI. Safe areas (notch, home indicator) are respected by default; `.ignoresSafeArea()` opts out. This replaces `systemBarsPadding()`.
7. HTTP (non-HTTPS) calls are blocked by **App Transport Security**. To call `http://localhost:8080` from the simulator during backend development, add an ATS exception (`NSAllowsLocalNetworking`) for debug builds only.
8. **No background work by default.** iOS suspends your app soon after it goes to the background. There's no WorkManager equivalent with the same freedom; look at `BGTaskScheduler` and background `URLSession` when you need it.
9. Swift 6 concurrency errors look scary. Start with `@MainActor` view models, struct models and `actor` caches.
10. Xcode's build errors can show up in the wrong file. Clean the build folder (⇧⌘K) and reset the package caches before you lose an hour. That's the iOS version of "invalidate caches and restart".

---

## 13. A learning path (about 4–6 weekends)

**Week 1 — Swift the language.** Read *The Swift Programming Language* (free, docs.swift.org), especially the chapters on Optionals, Structures and Classes, Enumerations, Protocols, Error Handling, Concurrency and Access Control. Do the exercises in a Swift Playground or on swift.org's Windows toolchain if you don't have a Mac yet.

**Week 2 — SwiftUI basics.** Apple's official *SwiftUI Tutorials* (developer.apple.com/tutorials/swiftui), then *Hacking with Swift: 100 Days of SwiftUI* (free) for daily practice.

**Week 3 — Urithiru-lite.** One Xcode app with no packages yet: `WeaponListView` + `WeaponDetailView` against `eldenring.fanapis.com`, with search, paging and pull-to-refresh. Use sections 4.2–4.7 as your guide.

**Week 4 — Architecture.** Move the code into a local SPM package with `Core`, `CoreEntity`, `ApiA` and `FeatureA` targets. Add the `Router`, the `AppContainer` and Swift Testing tests with a fake repository.

**Week 5 — Polish.** A `TabView` (Home · Weapons · Bosses · Favorites), favorites with SwiftData, dark mode, Dynamic Type, Dev/Prod schemes using `.xcconfig`.

**Week 6 — Ship.** TestFlight to your own phone. Then decide: native or KMP for the long term.

**References**
- Swift Book: docs.swift.org/swift-book
- Apple SwiftUI tutorials and the WWDC session videos (developer.apple.com/videos), especially "Meet async/await", "Discover Observation in SwiftUI" and "The SwiftUI cookbook for navigation"
- Hacking with Swift (hackingwithswift.com)
- Point-Free (pointfree.co) for advanced architecture
- Human Interface Guidelines (developer.apple.com/design/human-interface-guidelines), the iOS counterpart of Material guidelines

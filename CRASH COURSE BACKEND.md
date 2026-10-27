# Crash Course: Backend for an Android (Kotlin) Developer

A reading guide that teaches backend development by designing a **server for Urithiru**, in Kotlin, using the same tools you already use (coroutines, kotlinx-serialization, Gradle). Read it on your phone; no code needs to run. Companion: `CRASH COURSE IOS.md`.

---

## 0. Why Urithiru actually needs a backend

So far the app talks directly to other people's APIs. Several problems in this repo can only be solved by a server you own:

| Problem in the repo today | How a backend fixes it |
|---|---|
| The **Twitch client secret** must ship inside the app for `client_credentials`, and anyone can decompile an APK and read it. | The server keeps the secret, gets the token and calls IGDB. The app never sees the secret. |
| `SplashScreenViewModel` hardcodes the JSONBin `binId`. | The server serves `GET /onboarding` from its own database. You can change the content without an app release. |
| Paging guesses `isEndReached = size < 20`. | Your API returns `hasMore` / `nextPage` explicitly. |
| Favorites are planned to be local-only (Room). | The server stores favorites per user, so they sync across phones and iOS. |
| The Elden Ring fan API can be slow, change shape or go down. | The server caches it and returns a **stable shape you control**. This is the "Backend for Frontend" (BFF) pattern. |
| Android and iOS would each need their own copy of the business rules. | The rules live once, on the server. |

The course builds that server step by step.

---

## 1. Choosing a stack

| Option | Language | Why pick it | Why not |
|---|---|---|---|
| **Ktor** ⭐ | Kotlin | Made by JetBrains. Coroutines-first, kotlinx-serialization, small and explicit. Uses everything you already know. | Smaller job market than Spring. You assemble more pieces yourself. |
| **Spring Boot** | Kotlin or Java | The industry standard on the JVM. Huge ecosystem, many jobs, lots of "magic" annotations (feels like Hilt everywhere). | Heavier. The annotation magic hides what's happening, which is hard when you're learning. |
| Node.js (Express/NestJS) | TypeScript | Huge ecosystem. Serverless-friendly. | New language and runtime. |
| Go | Go | Simple, fast, cheap to host. | New language. |

**Recommendation:** learn the concepts with **Ktor** (this course), then read a Spring Boot tutorial. You'll see that ~80% is the same ideas with different annotations. The concepts (HTTP, SQL, auth, deployment) are identical in every stack.

---

## 2. The mental model: Android → backend

| Android (Urithiru) | Backend (Ktor) |
|---|---|
| One user, one process on one phone | **Thousands of requests at the same time**, one long-running process (or several copies) |
| `Application.onCreate` | `fun main()` / `Application.module()` |
| A screen (`@FeatureRoute`) | A **route**: `get("/weapons") { … }` |
| `ViewModel.onAction` | A route handler that calls a **service** |
| Repository (`WeaponRepositoryImpl`) | Repository. **The same thing!** It talks to the database or other APIs. |
| Retrofit interface (`WeaponApi`) | The **other side** of it: you *define* the endpoints that Retrofit calls. (You can still use Ktor **Client**, a Retrofit equivalent, to call upstream APIs.) |
| DataStore / Room | **PostgreSQL** (a real database server) + an SQL library |
| Hilt | Koin, Ktor's DI plugin, or manual wiring in `module()` |
| OkHttp interceptor (`LogcatInterceptor`) | Ktor **plugins** (middleware): logging, auth, CORS, compression, error mapping |
| `BuildConfig` flavors (`dev.properties`) | **Environment variables** + `application.conf` |
| Logcat | Server logs (Logback), then a log service |
| Crashlytics | Sentry, or your host's error tracking |
| APK / AAB | A **fat JAR** or a **Docker image** |
| Play Store | A cloud host (Railway, Render, Fly.io, Google Cloud Run, a VPS…) |
| `Dispatchers.Main` must never block | **No main thread**, but never block a request thread either. Use `suspend` and run JDBC on `Dispatchers.IO`. |
| State in the ViewModel survives recomposition | **Servers should be stateless.** Any copy of the server must be able to answer any request, so state lives in the database or cache, not in variables. |
| The app trusts its own UI | **The server trusts nothing.** Every input can be forged by anyone with `curl`. Validate everything. |

---

## 3. HTTP from the server side (the 15-minute version)

You've been calling HTTP for years. Now you answer it.

**A request** has a method, a path, query parameters, headers and an optional body:

```
GET /api/v1/weapons?name=moon&page=0&limit=20 HTTP/1.1
Host: api.urithiru.dev
Authorization: Bearer eyJhbGciOi...
Accept: application/json
```

**A response** has a status code, headers and a body:

```
HTTP/1.1 200 OK
Content-Type: application/json

{"data":[{"id":"17f69…","name":"Moonveil", …}],"page":0,"hasMore":true}
```

**Methods (REST conventions):**

| Method | Meaning | Urithiru example |
|---|---|---|
| `GET` | Read. Must not change anything. Safe to retry. | `GET /api/v1/weapons/{id}` |
| `POST` | Create, or "do an action" | `POST /api/v1/auth/login` |
| `PUT` | Replace a whole resource (idempotent) | `PUT /api/v1/me/favorites/{weaponId}` |
| `PATCH` | Partially update | `PATCH /api/v1/me` `{ "displayName": "…" }` |
| `DELETE` | Delete (idempotent) | `DELETE /api/v1/me/favorites/{weaponId}` |

*Idempotent* means doing it twice has the same effect as doing it once, so the app can retry it safely.

**Status codes you'll actually use:**

| Code | Meaning | Maps to your planned `AppError` |
|---|---|---|
| 200 OK / 201 Created / 204 No Content | Success | Success |
| 400 Bad Request | The input is invalid | `Http(400)` |
| 401 Unauthorized | Not logged in, or the token expired | `Unauthorized` → refresh the token |
| 403 Forbidden | Logged in but not allowed | `Http(403)` |
| 404 Not Found | The resource doesn't exist | `NotFound` |
| 409 Conflict | Duplicate, or a version conflict | `Http(409)` |
| 429 Too Many Requests | Rate limited | Retry later |
| 500 / 502 / 503 | Your bug / the upstream failed / overloaded | `Server(code)` |

The `6969` code in `BaseDataSource` exists because the client had to invent errors. When you own the server, **return real status codes plus a consistent error body**, and the app can map them cleanly.

---

## 4. Designing Urithiru's API (do this before writing code)

```
GET    /health                              → "OK" (for the host's health checks)

GET    /api/v1/onboarding                   → onboarding pages (replaces JSONBin)

GET    /api/v1/weapons?name=&category=&page=0&limit=20
GET    /api/v1/weapons/{id}
GET    /api/v1/bosses?page=0&limit=20
GET    /api/v1/bosses/{id}

GET    /api/v1/games/trending               → IGDB via the server-side Twitch token

POST   /api/v1/auth/login                   → { accessToken, refreshToken }
POST   /api/v1/auth/refresh                 → { accessToken, refreshToken }

GET    /api/v1/me/favorites                 → requires a login
PUT    /api/v1/me/favorites/{weaponId}
DELETE /api/v1/me/favorites/{weaponId}
```

**One response envelope for lists** (fixes the paging guess):

```json
{
  "data": [ ... ],
  "page": 0,
  "limit": 20,
  "total": 307,
  "hasMore": true
}
```

**One error shape for everything** (the app's `ErrorResponse(code, message)` already matches):

```json
{ "code": 404, "message": "Weapon 'abc' not found", "type": "NOT_FOUND" }
```

Design rules:
- Use nouns in paths (`/weapons`) and let the method be the verb.
- Version the API (`/api/v1`). **Old app versions stay installed for years**, so never break `v1`; add `v2` instead.
- Adding fields is safe because clients use `ignoreUnknownKeys`. Removing or renaming fields breaks old apps.
- Clamp limits on the server (`limit` at most 100). Never trust the client to be polite.

---

## 5. Project structure: Urithiru's Clean Architecture, server edition

You already know multi-module Gradle and convention plugins, so reuse that knowledge:

```
urithiru-server/
├── buildlogic/                 ← convention plugins (same idea as the app's)
├── shared-model/               ← @Serializable DTOs, KMP: used by server AND app ⭐
├── server-core/                ← error types, DB setup, HTTP client, config
├── feature-weapon/             ← routes + service + repository for weapons
├── feature-auth/
├── feature-favorite/
├── feature-game/               ← IGDB + Twitch token
└── server-app/                 ← main(), installs plugins, wires everything (like :app)
```

Inside one feature module, the layering is familiar:

```
feature-weapon/
├── route/        WeaponRoutes.kt         ← like your Screen: parses input, returns output
├── service/      WeaponService.kt        ← like a UseCase: business rules
├── repository/   WeaponRepository.kt     ← DB and/or upstream API
└── upstream/     EldenRingClient.kt      ← like WeaponRemoteDataSource (Ktor Client)
```

⭐ **`shared-model`** is the big win of a Kotlin backend: put `WeaponDto`, `PageResponse<T>` and `ErrorResponse` in a Kotlin Multiplatform module. Server and Android compile against the **same classes**, so a renamed field breaks the build instead of the app. (iOS can use it too through KMP; see the iOS course, section 11.)

To start a project: **start.ktor.io** (or IntelliJ IDEA → New Project → Ktor) generates it with the plugins you tick. Pick: Routing, ContentNegotiation + kotlinx.serialization, StatusPages, CallLogging, Authentication JWT, and Postgres/Exposed.

---

## 6. Your first Ktor server

```kotlin
// server-app/src/main/kotlin/Application.kt
fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    // Like SerializationModule: one shared Json
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
    install(CallLogging)            // logs every request: the server's LogcatInterceptor

    routing {
        get("/health") {
            call.respondText("OK")
        }
    }
}
```

Run `./gradlew run`, open `http://localhost:8080/health`, and you've written a backend.

Key ideas:
- `install(X)` adds a **plugin**, which wraps every request (like an OkHttp interceptor chain, but on the receiving side).
- `call` is the current request and response. Everything inside `get { }` is a **`suspend` lambda**, so coroutines work out of the box.
- Each request runs concurrently. Two users calling `/weapons` at the same moment run your handler twice, at the same time.

---

## 7. Routes, services and validation (the weapon feature)

```kotlin
// shared-model: the API contract (used by server and Android)
@Serializable
data class PageResponse<T>(
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val hasMore: Boolean
)

@Serializable
data class ErrorResponse(val code: Int, val message: String?, val type: String)
```

```kotlin
// feature-weapon/route/WeaponRoutes.kt  (≈ the Screen: thin, no business logic)
fun Route.weaponRoutes(service: WeaponService) {
    route("/api/v1/weapons") {
        get {
            val params = call.request.queryParameters
            val page = params["page"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
            val limit = (params["limit"]?.toIntOrNull() ?: 20).coerceIn(1, 100)
            val name = params["name"]?.trim()?.takeIf { it.isNotEmpty() }

            call.respond(service.list(name, page, limit))
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("Missing id")
            call.respond(service.detail(id))
        }
    }
}
```

```kotlin
// feature-weapon/service/WeaponService.kt  (≈ a UseCase)
class WeaponService(private val repository: WeaponRepository) {
    suspend fun list(name: String?, page: Int, limit: Int): PageResponse<Weapon> {
        val weapons = repository.findWeapons(name, page, limit + 1)   // fetch one extra
        return PageResponse(
            data = weapons.take(limit),
            page = page,
            limit = limit,
            hasMore = weapons.size > limit                             // no more guessing
        )
    }

    suspend fun detail(id: String): Weapon =
        repository.findWeapon(id) ?: throw NotFoundException("Weapon '$id' not found")
}
```

**Central error mapping**, the server-side version of "map errors in one place" from `FOR READING.md`:

```kotlin
install(StatusPages) {
    exception<BadRequestException> { call, e ->
        call.respond(HttpStatusCode.BadRequest, ErrorResponse(400, e.message, "BAD_REQUEST"))
    }
    exception<NotFoundException> { call, e ->
        call.respond(HttpStatusCode.NotFound, ErrorResponse(404, e.message, "NOT_FOUND"))
    }
    exception<Throwable> { call, e ->
        call.application.log.error("Unhandled error", e)       // log the details…
        call.respond(                                          // …but don't leak them to clients
            HttpStatusCode.InternalServerError,
            ErrorResponse(500, "Something went wrong", "INTERNAL")
        )
    }
}
```

Never send stack traces or SQL errors to clients. They help attackers and confuse users.

---

## 8. Calling other APIs: Ktor Client (your Retrofit replacement)

The server calls the Elden Ring API the way `api-eldenring` does today:

```kotlin
// server-core: one shared client (like NetworkModule's OkHttpClient)
val httpClient = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {   // the CLIENT plugin,
        json(Json { ignoreUnknownKeys = true })                               // not the server one
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 10_000
    }
}

// feature-weapon/upstream/EldenRingClient.kt  (≈ WeaponRemoteDataSourceImpl)
class EldenRingClient(private val client: HttpClient, private val baseUrl: String) {
    suspend fun weapons(name: String?, page: Int, limit: Int): ApiDto<List<WeaponResponse>> =
        client.get("${baseUrl}weapons") {
            parameter("page", page)
            parameter("limit", limit)
            name?.let { parameter("name", it) }
        }.body()
}
```

The `ApiDto` and `WeaponResponse` classes from `api-eldenring` work here unchanged. That's the benefit of staying in Kotlin.

**Cache the upstream API.** The fan API doesn't change often, so don't hit it on every request:

```kotlin
class CachedWeaponRepository(private val upstream: EldenRingClient) : WeaponRepository {
    private data class Entry(val weapons: List<Weapon>, val storedAt: Long)

    private val cache = ConcurrentHashMap<String, Entry>()   // thread-safe: many requests at once
    private val ttlMillis = 10 * 60 * 1000L

    override suspend fun findWeapons(name: String?, page: Int, limit: Int): List<Weapon> {
        val key = "$name|$page|$limit"
        cache[key]?.takeIf { System.currentTimeMillis() - it.storedAt < ttlMillis }?.let { return it.weapons }

        val fresh = upstream.weapons(name, page, limit).data.map { it.mapToWeapon() }
        cache[key] = Entry(fresh, System.currentTimeMillis())
        return fresh
    }
    // …
}
```

An in-memory cache disappears on restart and isn't shared between server copies. When you run more than one instance, move it to **Redis**.

---

## 9. Databases: PostgreSQL + SQL (the part that's genuinely new)

Room hid SQL behind DAOs. On the backend you should learn **plain SQL**, because every stack and every debugging session ends up there.

### 9.1 Tables

```sql
CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,              -- NEVER the plain password
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE favorites (
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weapon_id  TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, weapon_id)          -- a user can't favorite the same weapon twice
);

CREATE TABLE onboarding_pages (
    id          SERIAL PRIMARY KEY,
    position    INT NOT NULL,
    title       TEXT NOT NULL,
    description TEXT NOT NULL,
    image_url   TEXT
);
```

### 9.2 Queries you'll write all the time

```sql
-- a user's favorites, newest first
SELECT weapon_id FROM favorites WHERE user_id = $1 ORDER BY created_at DESC;

-- add a favorite and ignore duplicates (makes PUT idempotent)
INSERT INTO favorites (user_id, weapon_id) VALUES ($1, $2) ON CONFLICT DO NOTHING;

-- remove one
DELETE FROM favorites WHERE user_id = $1 AND weapon_id = $2;

-- paging
SELECT * FROM onboarding_pages ORDER BY position LIMIT 20 OFFSET 0;
```

⚠️ **Never build SQL by concatenating user input** (`"... WHERE name = '" + name + "'"`). That's **SQL injection**, the most famous backend vulnerability. Always use parameters (`$1`, `?`) or a library that does it for you.

### 9.3 Talking to Postgres from Kotlin

Common choices:
- **Exposed** (JetBrains): a Kotlin DSL over SQL. Feels the most like Room.
- **jOOQ**: type-safe SQL generated from your schema.
- **SQLDelight**: you write `.sq` files with SQL and it generates Kotlin. It's also KMP, so it can run on Android too.

An Exposed sketch (imports differ between Exposed versions, so check the docs for yours):

```kotlin
object Favorites : Table("favorites") {
    val userId = uuid("user_id")
    val weaponId = text("weapon_id")
    override val primaryKey = PrimaryKey(userId, weaponId)
}

class FavoriteRepository(private val db: Database) {
    suspend fun favorites(userId: UUID): List<String> = dbQuery {
        Favorites.selectAll()
            .where { Favorites.userId eq userId }
            .map { it[Favorites.weaponId] }
    }

    suspend fun add(userId: UUID, weaponId: String) = dbQuery {
        Favorites.insertIgnore {
            it[Favorites.userId] = userId
            it[Favorites.weaponId] = weaponId
        }
    }

    // JDBC is blocking, so run it on Dispatchers.IO (just like Room queries off the main thread)
    private suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) { transaction(db) { block() } }
}
```

### 9.4 Things Room did for you that you now do yourself

- **Connection pool (HikariCP):** opening a DB connection is slow, so keep a pool of about 10 and reuse them.
- **Migrations (Flyway or Liquibase):** versioned SQL files (`V1__create_users.sql`, `V2__add_favorites.sql`) applied at startup. This is Room's `Migration(1, 2)`, but **you can never edit an applied file**; you always add a new one.
- **Transactions:** wrap multi-step changes so they all happen or none do, e.g. "create the user + create default settings".
- **Indexes:** `CREATE INDEX ON favorites (user_id);` makes lookups fast. Add one for every column you filter by often.

---

## 10. Authentication

### 10.1 The flow

```
App                                Server
 │  POST /auth/login {email, pw}     │
 │ ─────────────────────────────────►│ check password hash
 │  {accessToken (15 min),           │ sign a JWT
 │   refreshToken (30 days)}         │
 │ ◄─────────────────────────────────│
 │                                   │
 │  GET /me/favorites                │
 │  Authorization: Bearer <access>   │
 │ ─────────────────────────────────►│ verify the JWT signature → userId
 │  200 [...]                        │
 │ ◄─────────────────────────────────│
 │                                   │
 │  (15 min later) 401               │
 │  POST /auth/refresh {refresh}     │ ← the app's OkHttp Authenticator does this
 │ ─────────────────────────────────►│   (FOR READING.md, section 4, step 6)
```

- A **JWT** is a signed JSON blob (`{"sub":"<userId>","exp":…}`). The server can verify it without a database lookup. It is *signed*, **not encrypted**, so never put secrets in it.
- Store **password hashes** made with bcrypt or Argon2, never plain passwords or a simple SHA hash.
- On Android, store the tokens in encrypted storage. On iOS, use the Keychain.

### 10.2 In Ktor

```kotlin
install(Authentication) {
    jwt("auth-jwt") {
        realm = "urithiru"
        verifier(
            JWT.require(Algorithm.HMAC256(config.jwtSecret))
                .withIssuer("urithiru")
                .build()
        )
        validate { credential ->
            if (credential.payload.subject != null) JWTPrincipal(credential.payload) else null
        }
    }
}

routing {
    authenticate("auth-jwt") {                       // everything inside requires a valid token
        route("/api/v1/me/favorites") {
            get {
                val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
                call.respond(favoriteService.favorites(userId))
            }
            put("/{weaponId}") {
                val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
                favoriteService.add(userId, call.parameters["weaponId"]!!)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
```

**The most important security rule:** the user ID comes **from the verified token**, never from the request body or URL. Otherwise anyone can read anyone's favorites by changing an ID (this bug is called IDOR).

### 10.3 Don't build passwords yourself in production

For a real app, let an identity provider handle passwords, email verification and "forgot password": **Firebase Auth**, **Sign in with Apple** / Google Sign-In, Supabase Auth, Auth0. The app signs in with them, sends their **ID token** to your server, and your server verifies it and finds or creates the user. Build your own login once to learn the concepts, then outsource it.

---

## 11. The Twitch/IGDB proxy: moving `api-twitch` to the server

Today `api-twitch` asks the *app* for a `clientId` and `clientSecret`. On the server it becomes:

```kotlin
class TwitchTokenProvider(
    private val client: HttpClient,
    private val clientId: String,          // from environment variables, never from git
    private val clientSecret: String
) {
    private val mutex = Mutex()            // many requests may need a token at once
    private var token: String? = null
    private var expiresAt = 0L

    suspend fun token(): String = mutex.withLock {
        val now = System.currentTimeMillis()
        token?.takeIf { now < expiresAt - 60_000 }?.let { return it }   // reuse until ~1 min before expiry

        val response: TwitchOAuthTokenResponse = client.post("https://id.twitch.tv/oauth2/token") {
            parameter("client_id", clientId)
            parameter("client_secret", clientSecret)
            parameter("grant_type", "client_credentials")
        }.body()

        token = response.accessToken
        expiresAt = now + response.expiresIn * 1000
        response.accessToken
    }
}

class GameService(private val client: HttpClient, private val tokens: TwitchTokenProvider, private val clientId: String) {
    suspend fun trending(): List<GameDto> =
        client.post("https://api.igdb.com/v4/games") {
            header("Client-ID", clientId)
            bearerAuth(tokens.token())
            setBody("fields name,cover.url,total_rating; sort total_rating_count desc; limit 20;")
        }.body()
}
```

Notice:
- The **`Mutex`** matters. Without it, 50 simultaneous requests after the token expires would request 50 tokens. This is the server-side cousin of the `collectApi` race in `SUMMARY.md`: on a server, concurrency is the normal case.
- `TwitchOAuthTokenResponse` is the exact class from `api-twitch`. Move it to `shared-model` or a server module.
- The app now just calls `GET /api/v1/games/trending` with no Twitch code, no secret and no token refresh. The whole `api-twitch` module and `RefreshTokenInterface` collapse into one Retrofit method.

---

## 12. Configuration and secrets (server "flavors")

`application.conf` (HOCON format) with environment-variable overrides:

```hocon
ktor {
    deployment {
        port = 8080
        port = ${?PORT}                       # the host sets PORT; the ?-form makes it optional
    }
    application {
        modules = [ com.septianbeneran.urithiru.server.ApplicationKt.module ]
    }
}

database {
    url = ${DATABASE_URL}                     # required: fails at startup if missing
    user = ${DATABASE_USER}
    password = ${DATABASE_PASSWORD}
}

jwt {
    secret = ${JWT_SECRET}
}

twitch {
    clientId = ${TWITCH_CLIENT_ID}
    clientSecret = ${TWITCH_CLIENT_SECRET}
}
```

Read values in code with `environment.config.property("twitch.clientId").getString()`.

- `dev` / `staging` / `prod` are **different sets of environment variables**, not different builds. **The same Docker image** runs everywhere; only the config changes. (Contrast this with Android flavors, where each flavor is a different APK.)
- Locally, keep a `.env` file (git-ignored) or set the variables in your IntelliJ run configuration. In production, use the host's secret settings.
- Fail fast: if a required variable is missing, crash at startup, not on the first request at 3 a.m.

---

## 13. Testing

Ktor has an in-memory test server, so you don't need a real port or network:

```kotlin
class WeaponRoutesTest {
    @Test
    fun `list returns hasMore when there is another page`() = testApplication {
        application {
            testModule(weaponRepository = FakeWeaponRepository(count = 21))   // fakes, like on Android
        }

        val response = client.get("/api/v1/weapons?limit=20")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<PageResponse<Weapon>>(response.bodyAsText())
        assertEquals(20, body.data.size)
        assertTrue(body.hasMore)
    }

    @Test
    fun `favorites require a token`() = testApplication {
        application { testModule() }
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/v1/me/favorites").status)
    }
}
```

- **Upstream APIs:** Ktor Client's `MockEngine` returns canned JSON, like a fake Retrofit service.
- **Database tests:** use **Testcontainers** to start a real throwaway Postgres in Docker. Don't fake SQL; most backend bugs live in queries.
- Writing a service with a fake repository is a plain JUnit test, exactly as on Android.

---

## 14. Deployment

### 14.1 Package it

The Ktor Gradle plugin provides:
- `./gradlew buildFatJar` → one runnable `.jar` (`java -jar server.jar`)
- `./gradlew buildImage` / `publishImageToLocalRegistry` → a Docker image

Or write a Dockerfile yourself:

```dockerfile
# build stage
FROM gradle:jdk21 AS build
WORKDIR /src
COPY . .
RUN gradle :server-app:buildFatJar --no-daemon

# run stage: small image, no build tools
FROM eclipse-temurin:21-jre
COPY --from=build /src/server-app/build/libs/*-all.jar /app/server.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/server.jar"]
```

**Docker in one sentence:** an image is "a tiny Linux machine with exactly my app and its Java version", so it runs the same on your laptop and in the cloud. Learn `docker build`, `docker run -p 8080:8080 -e KEY=value image`, and `docker compose` (to run your server + Postgres together locally).

### 14.2 Host it

| Need | Easy options |
|---|---|
| Run the container | Railway, Render, Fly.io, Google Cloud Run (all have free or cheap tiers) |
| Postgres | Neon, Supabase, or the host's managed Postgres |
| Domain + HTTPS | The host usually gives you HTTPS automatically. Never serve a real API over plain HTTP. |
| Error tracking | Sentry |
| Uptime | A `/health` check from the host, or an uptime monitor |

### 14.3 CI/CD

A GitHub Actions workflow: on every PR → `./gradlew test`; on merge to `main` → build the image → deploy. Run database migrations automatically at startup (Flyway) or as a deploy step.

---

## 15. Connecting the Android app to your server

1. Add `URITHIRU_BASE_URL` to each `productFlavorProperties/*.properties` file, and a `@UrithiruNetwork` qualifier plus a `Retrofit` provider in `NetworkModule` (the "Add a new backend" recipe in `SUMMARY.md`).
2. **From the Android emulator, `localhost` is the emulator itself.** Use `http://10.0.2.2:8080/api/v1/`. On a real phone, use your laptop's LAN IP.
3. Plain `http://` is blocked by default on Android 9+. Allow cleartext **only in dev**, using a `network_security_config.xml` for the debug build. (On the iOS simulator `localhost` works, but App Transport Security needs an exception; see the iOS course, gotcha 7.)
4. Replace `api-twitch` + JSONBin with calls to your own endpoints. Use the `shared-model` DTOs in the `api-*` modules.
5. Add an OkHttp `Authenticator` that calls `/auth/refresh` on a 401 (`FOR READING.md`, section 4, step 6).

---

## 16. Production concerns (a checklist to grow into)

- **Validation** on every input: lengths, ranges, formats. Reject early with 400.
- **Rate limiting** (Ktor's `RateLimit` plugin) on login and expensive endpoints.
- **CORS** (Ktor's `CORS` plugin) only matters if a *website* calls your API. Mobile apps don't need it.
- **Logging:** structured logs with a request ID per call (the `CallId` plugin) so you can follow one request through the logs. Never log passwords or tokens. (Same lesson as `LogcatInterceptor` logging `Authorization` headers.)
- **Timeouts** on every upstream call and DB query.
- **Pagination** on every list endpoint. Never return "all rows".
- **Backups** of the database. Turn them on and test restoring once.
- **Statelessness:** no user data in server memory, so you can run 2+ copies behind a load balancer.
- **Graceful shutdown:** let in-flight requests finish when deploying.
- **Account deletion endpoint.** App Store and Play policies require it if users can sign up.

---

## 17. Glossary for Android developers

| Term | Meaning |
|---|---|
| Endpoint | One method + path, e.g. `GET /weapons` |
| Middleware / plugin | Code that wraps every request: logging, auth, errors (≈ an interceptor) |
| Idempotent | Safe to repeat: PUT, DELETE, GET |
| Stateless | The server keeps no per-user memory between requests |
| Connection pool | Reused database connections (HikariCP) |
| Migration | A versioned schema change script (Flyway) |
| ORM / DSL | A library that maps tables to Kotlin (Exposed, Hibernate) |
| JWT | A signed token that proves who the user is |
| BFF | Backend for Frontend: an API shaped for your app, hiding other services |
| Load balancer | Spreads requests across several copies of the server |
| Container / image | A packaged app + runtime (Docker) |
| CI/CD | Automatic test + deploy on every push |
| p95 latency | 95% of requests are faster than this; the number to watch |

---

## 18. A learning path (about 5–6 weekends)

**Week 1 — HTTP + Ktor basics.** Generate a project at start.ktor.io. Build `/health` and `GET /api/v1/weapons` returning hardcoded data. Call it with `curl`, Postman or Bruno. Read the Ktor docs on Routing, Serialization and Status Pages.

**Week 2 — Proxy + cache.** Make `/weapons` call the Elden Ring API through Ktor Client, add the in-memory cache and the `PageResponse` envelope. Point the Android app's `dev` flavor at `10.0.2.2:8080`.

**Week 3 — SQL.** Install Postgres (or `docker run postgres`). Learn SELECT, INSERT, UPDATE, DELETE, JOIN, indexes and transactions with any free SQL tutorial. Add Flyway + Exposed (or SQLDelight), and move onboarding pages from JSONBin into a table.

**Week 4 — Auth.** Register/login with bcrypt + JWT, then the favorites endpoints protected by `authenticate`. Add the OkHttp `Authenticator` in the Android app.

**Week 5 — Twitch/IGDB + tests.** Build `TwitchTokenProvider` and `/games/trending`. Write `testApplication` tests and one Testcontainers test.

**Week 6 — Ship.** Dockerfile, deploy to Railway/Render/Fly.io with managed Postgres, set the secrets, and point the `prod` flavor at it. Then read a Spring Boot "REST API with Kotlin" guide and map each piece back to what you built.

**References**
- Ktor docs: ktor.io/docs
- Exposed: github.com/JetBrains/Exposed · SQLDelight: sqldelight.github.io
- PostgreSQL tutorial: postgresql.org/docs/current/tutorial.html
- HTTP reference: developer.mozilla.org/en-US/docs/Web/HTTP
- OWASP API Security Top 10: owasp.org (the classic list of what goes wrong)
- Spring Boot + Kotlin: spring.io/guides/tutorials/spring-boot-kotlin

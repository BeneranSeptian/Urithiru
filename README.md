# ⚔️ Urithiru Project
**One project to rule them all.**

A modular Android application template built with Clean Architecture principles.  
Designed to be scalable, maintainable, and easy to extend — even for beginners.

## 🧰 Tech Stack
* Jetpack Compose for UI creation
* Retrofit for API call
* Hilt for dependency injection
* Kotlin Coroutines for asynchronous programming

## 📦 Prerequisites

* Gradle 9.3.1
* JDK 21
* Minimum Android SDK 24

## 🧱 Project Modules

The project contains the following modules:

* `:api-a`
* `:api-b`
* `:app`
* `:buildlogic`
* `:core`
* `:core-entity`
* `:core-navigation`
* `:core-ui`
* `:feature-a`
* `:feature-b`
* `:feature-splash`

## 🏗 Architecture Flow

### 💾 Data Flow
```mermaid
flowchart LR

    subgraph Presentation Layer
        UI[Screen]
        VM[ViewModel]
    end

    subgraph Domain Layer
        UC[Use Case]
        REPO_INT[Repository]
    end

    subgraph Data Layer
        REPO_IMPL[Repository Implementation]
        RDS[Data Source]
        API[Retrofit API]
    end

    UI --> VM
    VM --> UC
    UC --> REPO_INT
    REPO_INT --> REPO_IMPL
    REPO_IMPL --> RDS
    RDS --> API

    API --> RDS
    RDS --> REPO_IMPL
    REPO_IMPL --> UC
    UC --> VM
    VM --> UI
```

### 🏭 Screen Route Generator
```mermaid
flowchart TD
    subgraph FeatureModule ["Feature Module"]
        A["Create Composable Screen (WeaponDetailRoute)"] --> B("@FeatureRoute")
    end

    subgraph KSPFeature ["KSP Output: Feature Level"]
        B -->|Generates route| C[weaponDetailRouteGenerated]
        B -->|Generates graph| D[featureANavGraph]
        C -.->|Called inside| D
        D --> E("@FeatureGraph")
    end

    subgraph KSPApp ["KSP Output: App Level"]
        E -->|Scanned by KSP| F[registerAllFeatureGraphs]
        F -.->|Aggregates| G[featureANavGraph]
        F -.->|Aggregates| H[featureBNavGraph]
    end

    subgraph AppModule ["App Module"]
        I[(NavHost Chamber)] -->|Executes| F
    end
```

<details>
<summary><h2>⚙️ Creating New Endpoint For API Call</h2></summary>

### 1️⃣ Changing the Base URL

The Base URL is managed per environment using property files in the `productFlavorProperties` folder.

1.  Navigate to `productFlavorProperties/`.
2.  Open the file corresponding to the environment you want to change (e.g., `dev.properties`, `prod.properties`).
3.  Update the `BASE_URL` value:
    ```properties
    BASE_URL=https://your-new-api-url.com/api/
    ```
4.  Sync the project with Gradle. The `BuildConfig.BASE_URL` will be updated automatically.

### 2️⃣ Creating a New API Endpoint

To add a new API call, follow these steps (using the `api-a` module as an example):

#### Step A: Define the Data Transfer Object (DTO)
Create your response model in `api-a/src/main/java/.../data/remote/dto/`.

```kotlin
data class YourResponse(
    val id: String,
    val name: String
)
```

#### Step B: Add the API Interface
Create new API interface (e.g., `YourApi.kt`) and add the endpoint function.

```kotlin
interface YourApi {
    @GET
    suspend fun getYourData(
        @Url url: String
    ): Response<ApiDto<YourResponse>>
}
```

#### Step C: Update the Remote Data Source
Add the call to your `RemoteDataSource` interface and its implementation.

**Interface:**
```kotlin
suspend fun getYourData(): ApiResult<ApiDto<List<YourResponse>>>
```

**Implementation:**
```kotlin
override suspend fun getYourData(): ApiResult<ApiDto<WeaponResponse>> =
    getResult { api.getYourData("your/end/point") }
```

#### Step D: Expose through Repository and Use Case
Finally, expose the data through the Repository and create a Use Case to be consumed by the ViewModel in the `feature` module.
</details>

<details>
<summary><h2>📱 Creating a New Screen Route</h2></summary>

To add a new screen and automatically wire it into the navigation graph, follow these steps:

#### Step A: Define the Feature Navigation Graph
First, declare the navigation graph for your feature in the `core-navigation:graph` module. This must be marked with `@Serializable`.

```kotlin
@Serializable
object FeatureANavGraph
```

#### Step B: Define the Route Parameters
Next, define the specific route for your screen in the `core-navigation:routeparams` module. This also requires the `@Serializable` annotation. Use an `object` for a route without arguments, or a `data class` if you need to pass data.

```kotlin
@Serializable
object WeaponDetailRoute
```

#### Step C: Create and Annotate the Composable
In your `feature` module, create your standard Jetpack Compose UI function. Tag it with the `@FeatureRoute` annotation, linking the route parameters you just created. 

*Note: If this screen is the starting point for the feature, be sure to declare the `navGraph` parameter as well.*

```kotlin
@FeatureRoute(
    routeParams = WeaponDetailRoute::class,
    navGraph = FeatureANavGraph::class // Add this ONLY if it's the start destination
)
@Composable
fun WeaponDetailScreen(navigator: Navigator) {
    // Your UI implementation here
}
```

#### Step D: Build the Project
Run a project build (or rebuild) so KSP can process the annotations. 

During the build, KSP will automatically generate:
1.  The route extension for your specific screen (e.g., `weaponDetailRouteGenerated`).
2.  The updated feature-level graph builder (e.g., `featureANavGraph`) containing your new route.

#### Step E: Automatic Registration
You don't need to manually register the screen in the App module! 

The KSP processor automatically tags the generated feature graph with `@FeatureGraph`. The App module will scan for this and automatically include your new screen in the main `NavHost` via the generated `registerAllFeatureGraphs` function.

</details>

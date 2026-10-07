import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryProductFlavor
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

/**
 * Android setup shared by the application and every library module:
 * SDK levels, Java 21, BuildConfig/resValues, and the product flavors.
 */
internal fun CommonExtension.configureAndroid(project: Project) {
    compileSdk = AppConfig.compileSdk
    defaultConfig.minSdk = AppConfig.minSdk

    buildFeatures.buildConfig = true
    buildFeatures.resValues = true

    compileOptions.sourceCompatibility = JavaVersion.VERSION_21
    compileOptions.targetCompatibility = JavaVersion.VERSION_21

    configureFlavors(project)
}

/**
 * Creates one flavor per [ProductFlavor]. Every key in `productFlavorProperties/<flavor>.properties`
 * becomes a `BuildConfig` String field, and `APP_NAME` also becomes the `app_name` string resource.
 * Only the application gets an `applicationIdSuffix`.
 */
private fun CommonExtension.configureFlavors(project: Project) {
    flavorDimensions += AppConfig.flavorDimension
    val isApplication = this is ApplicationExtension

    ProductFlavor.entries.forEach { flavor ->
        val flavorProperties = project.flavorProperties(flavor)

        productFlavors.create(flavor.flavor).apply {
            dimension = AppConfig.flavorDimension

            resValue("string", "app_name", flavorProperties["APP_NAME"] ?: "My App")

            flavorProperties.forEach { (key, value) ->
                buildConfigField("String", key, "\"$value\"")
            }

            // AGP backs both flavor types with one class, so branch on the module type, not on `this`
            val isDefaultFlavor = flavor == ProductFlavor.DEV
            if (isApplication) {
                this as ApplicationProductFlavor
                isDefault = isDefaultFlavor
                flavor.suffix?.let { applicationIdSuffix = it }
            } else {
                (this as LibraryProductFlavor).isDefault = isDefaultFlavor
            }
        }
    }
}

private fun Project.flavorProperties(flavor: ProductFlavor): Map<String, String> =
    Properties().apply {
        val propertiesFile = file("${rootDir}/productFlavorProperties/${flavor.flavor}.properties")
        if (propertiesFile.exists()) {
            FileInputStream(propertiesFile).use { load(it) }
        }
    }.entries.associate { entry -> entry.key.toString() to entry.value.toString() }

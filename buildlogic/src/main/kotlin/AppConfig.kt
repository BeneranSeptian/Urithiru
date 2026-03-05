object AppConfig {
    const val projectNameSpace = "com.septianbeneran.urithiru"
    const val compileSdk = 36
    const val minSdk = 24
    const val targetSdk = 35
    const val versionCode = 1
    const val flavorDimension = "environment"
}

enum class ProductFlavor(val flavor: String, val suffix: String? = null) {
    DEV("dev", ".dev"),
    UAT("uat", ".uat"),
    BETA("beta", ".beta"),
    PROD("prod")
}
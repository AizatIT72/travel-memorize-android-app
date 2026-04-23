import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.dagger)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.core.build_config.impl"

    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        val properties = Properties()
        val propertiesFile = rootProject.file("local.properties")

        val apiKey = if (propertiesFile.exists()) {
            properties.load(FileInputStream(propertiesFile))
            properties.getProperty("MAPBOX_KEY", "")
        } else {
            ""
        }

        buildConfigField("String", "API_BASE_URL", "\"https://api.mapbox.com/\"")
        buildConfigField("String", "MAPBOX_KEY", "\"$apiKey\"")
    }
}

dependencies {
    implementation(project(path = ":core:build-config:api"))
}

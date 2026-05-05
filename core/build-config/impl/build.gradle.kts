import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.dagger)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.impl"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val properties = Properties()
        val propertiesFile = rootProject.file("local.properties")

        if (propertiesFile.exists()) {
            properties.load(FileInputStream(propertiesFile))
        }

        val mapboxAccessToken = properties.getProperty("MAPBOX_ACCESS_TOKEN", "")

        buildConfigField("String", "MAPBOX_BASE_URL", "\"https://api.mapbox.com/\"")
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxAccessToken\"")
    }
}

dependencies {
    implementation(project(path = ":core:build-config:api"))
}
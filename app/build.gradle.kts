plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.compose)
    alias(libs.plugins.app.dagger)
}

android {
    namespace = "ru.itis.android.travel_memorize_app"

    defaultConfig {
        applicationId = "ru.itis.android.travel_memorize_app"
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.x.activity.compose)
    implementation(libs.retrofit)

    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(path = ":core:build-config:api"))
    implementation(project(path = ":core:build-config:impl"))
    implementation(project(path = ":core:utils"))
}
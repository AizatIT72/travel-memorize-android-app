plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.compose)
    alias(libs.plugins.app.dagger)
    alias(libs.plugins.google.services)
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

    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:map"))
    implementation(project(path = ":core:build-config:api"))
    implementation(project(path = ":core:build-config:impl"))
    implementation(project(path = ":core:utils"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.x.activity.compose)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
}
plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.auth"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
}
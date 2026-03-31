plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(project(":core:build-config:api"))
}
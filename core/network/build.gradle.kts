plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.dagger)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")

    implementation(project(":core:build-config:api"))
    implementation(project(":core:domain"))
}
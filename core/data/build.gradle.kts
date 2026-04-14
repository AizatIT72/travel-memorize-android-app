plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.firebase)
    alias(libs.plugins.app.dagger)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.data"
}

dependencies {
    implementation(project(path = ":core:domain"))
    implementation(project(path = ":core:network"))
    implementation(project(path = ":core:build-config:api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
}
plugins {
    alias(libs.plugins.app.android.library)
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
}
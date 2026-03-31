plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.utils"
}

dependencies {
    implementation(project(path = ":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit)
}
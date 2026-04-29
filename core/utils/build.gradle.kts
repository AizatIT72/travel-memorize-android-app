plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.dagger)
    alias(libs.plugins.app.compose)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.utils"
}

dependencies {
    implementation(project(path = ":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel)
}
plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.compose)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.feature.navigation"

}

dependencies {
    implementation(project(":core:ui"))

    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.x.activity.compose)
    implementation(libs.navigation.compose)
}
plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.compose)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.x.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
}
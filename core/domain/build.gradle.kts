plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.compose)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.domain"
}

dependencies {

    implementation(libs.javax.inject)
}
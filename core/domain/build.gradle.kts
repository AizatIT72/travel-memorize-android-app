plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.domain"
}

dependencies {

    implementation(project(":core:network"))
    implementation(libs.javax.inject)
}
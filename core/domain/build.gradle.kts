plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "ru.itis.android.travel_memorize_app.domain"
}

dependencies {

    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.retrofit)
    implementation(libs.javax.inject)
}
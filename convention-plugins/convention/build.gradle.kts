import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "ru.itis.android.core.plugin.ext"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = libs.plugins.app.android.application.get().pluginId   // ← app.android.application
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.app.android.library.get().pluginId       // ← app.android.library
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidAppCompose") {
            id = libs.plugins.app.compose.get().pluginId               // ← app.compose
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.app.hilt.get().pluginId                  // ← app.hilt
            implementationClass = "HiltConventionPlugin"
        }
        register("androidDagger") {
            id = libs.plugins.app.dagger.get().pluginId                // ← app.dagger
            implementationClass = "DaggerConventionPlugin"
        }
        register("detekt") {
            id = "app.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}
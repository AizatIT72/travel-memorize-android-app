import java.util.Properties

include(":feature:navigation")


pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
val localProperties = Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")

            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                username = "mapbox"
                password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN", "")
            }
        }
    }
}

rootProject.name = "travel_memorize_app"
include(":app")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:build-config:api")
include(":core:build-config:impl")
include(":core:utils")
include(":core:ui")
include(":feature:auth")
include(":feature:map")
include(":feature:memory")
include(":feature:friends")
include(":feature:profile")

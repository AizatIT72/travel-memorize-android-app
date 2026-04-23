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
                password = providers.gradleProperty("MAPBOX_DOWNLOAD_TOKEN").orNull ?: ""
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

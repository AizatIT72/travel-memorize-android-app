import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import ru.itis.android.core.plugin.ext.libs

class DetektConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.gitlab.arturbosch.detekt")

            dependencies {
                "detektPlugins"(libs.findLibrary("detekt.formatting").get())
            }

            extensions.configure<DetektExtension> {
                config.setFrom("$rootDir/config/detekt/detekt.yml")
                buildUponDefaultConfig = true
                autoCorrect = true
            }
        }
    }
}
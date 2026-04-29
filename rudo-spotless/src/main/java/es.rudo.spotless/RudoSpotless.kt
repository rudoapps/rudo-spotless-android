package es.rudo.spotless

import com.diffplug.gradle.spotless.SpotlessExtension
import es.rudo.spotless.rules.configureKotlinGradleRules
import es.rudo.spotless.rules.configureKotlinRules
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class RudoSpotless : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply("com.diffplug.spotless")

        val spotlessExt =
            project.extensions.findByType(SpotlessExtension::class.java)

        if (spotlessExt != null) {
            project.extensions.configure<SpotlessExtension> {
                this.configureKotlinRules(project = project)
                this.configureKotlinGradleRules(project = project)
            }
        }
    }
}

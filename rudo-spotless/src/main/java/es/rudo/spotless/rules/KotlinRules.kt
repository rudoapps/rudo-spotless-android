package es.rudo.spotless.rules

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project

fun SpotlessExtension.configureKotlinRules(project: Project) {
    this.kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**")

        ktlint("1.4.0")
            .editorConfigOverride(
                mapOf(
                    "indent_style" to "space",
                    "indent_size" to "4",

                    "end_of_line" to "lf",
                    "charset" to "utf-8",
                    "trim_trailing_whitespace" to "true",
                    "insert_final_newline" to "true",

                    "ktlint_standard_function-naming" to "disabled",

                    "max_line_length" to "100"
                )
            )

        replaceRegex(
            "collapse-blank-lines",
            "(?m)(\\r?\\n[ \\t]*){3,}",
            "\n\n"
        )
    }
}

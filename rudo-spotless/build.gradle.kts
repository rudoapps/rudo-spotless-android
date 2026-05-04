import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    signing
}

group = "es.rudo"
version = "1.0.0"

kotlin {
    jvmToolchain(jdkVersion = 21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

base {
    archivesName.set("rudo-spotless")
}

gradlePlugin {
    plugins {
        create("rudoSpotless") {
            id = "es.rudo.spotless"
            implementationClass = "es.rudo.spotless.RudoSpotless"
            displayName = "Custom spotless library for Rudo"
            description = "Spotless rules for Rudo"
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Rudo Spotless Gradle Plugin")
            description.set("Spotless rules for Rudo projects.")
            url.set("https://github.com/rudoapps/rudo-spotless-android")

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("rudoapps")
                    name.set("rudo")
                    email.set("developer@rudo.es")
                }
            }

            scm {
                connection.set("scm:git:https://github.com/rudoapps/rudo-spotless-android.git")
                developerConnection.set("scm:git:ssh://git@github.com/rudoapps/rudo-spotless-android.git")
                url.set("https://github.com/rudoapps/rudo-spotless-android")
            }
        }
    }

    repositories {
        maven {
            name = "mavenCentralBundle"
            url = rootProject.layout.buildDirectory
                .dir("maven-central-bundle")
                .get()
                .asFile
                .toURI()
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.4.0")
}

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

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.4.0")
}

publishing {
    // Maven Local repository for testing
    publications {
        create<MavenPublication>("rudoSpotless") {
            from(components["java"])
            groupId = "es.rudo.spotless"
            artifactId = "rudo-spotless-gradle-plugin"
            version = "1.0.0"
        }
    }
}

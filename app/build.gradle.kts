import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    id("es.rudo.spotless")
}

android {
    namespace = "es.rudo.spotless"
    compileSdk {
        version = release(version = 36)
    }

    defaultConfig {
        applicationId = "es.rudo.spotless"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(jdkVersion = 21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

androidComponents {
    onVariants { variant ->
        variant.sources.kotlin?.addStaticSourceDirectory(
            "build/generated/ksp/${variant.name}/kotlin",
        )
    }
}

private val gitHooksDirPath = rootProject.layout.projectDirectory
    .dir(".git/hooks")
    .asFile
    .absolutePath

private val preCommitHookPath = rootProject.layout.projectDirectory
    .file(".git/hooks/pre-commit")
    .asFile
    .absolutePath

private val commitMsgHookPath = rootProject.layout.projectDirectory
    .file(".git/hooks/commit-msg")
    .asFile
    .absolutePath

private val installPreCommitGitHook by tasks.register<Copy>(name = "installPreCommitGitHook") {
    inputs.property("gitHooksDirPath", gitHooksDirPath)
    inputs.property("targetHookPath", preCommitHookPath)

    from(rootProject.layout.projectDirectory.file("hooks/pre-commit"))
    into(rootProject.layout.projectDirectory.dir(".git/hooks"))

    filePermissions {
        unix("555")
    }

    onlyIf {
        val hookFile = File(inputs.properties["targetHookPath"] as String)

        if (hookFile.exists()) {
            println("\uD83D\uDFE2 pre-commit git hook already installed.")
            false
        } else {
            println("\uD83D\uDD04 Installing pre-commit git hook...")
            true
        }
    }

    doLast {
        val hooksDir = File(inputs.properties["gitHooksDirPath"] as String)
        val preCommit = File(inputs.properties["targetHookPath"] as String)

        require(value = hooksDir.exists()) {
            "\uD83D\uDD34 Git hooks directory does not exist: $hooksDir"
        }

        require(value = preCommit.exists()) {
            "\uD83D\uDD34 pre-commit git hook was not installed. Expected at: $preCommit"
        }

        if (!preCommit.canExecute()) preCommit.setExecutable(true)

        println("\uD83D\uDFE2 pre-commit git hook installed successfully.")
        println("\uD83D\uDCCC Location: $hooksDir")

        hooksDir
            .listFiles()
            ?.filter { file -> file.isFile }
            ?.sortedBy { file -> file.name }
            ?.find { hook ->
                hook.name == "pre-commit"
            }?.let { hook ->
                val isExecutable =
                    if (hook.canExecute()) {
                        "✅"
                    } else {
                        "❌"
                    }
                println("\uD83D\uDCDD ${hook.name}: isExecutable = $isExecutable")
            }
    }
}

private val installCheckCommitMessageGitHook by tasks.register<Copy>(
    name = "installCheckCommitMessageGitHook",
) {
    inputs.property("gitHooksDirPath", gitHooksDirPath)
    inputs.property("targetHookPath", commitMsgHookPath)

    from(rootProject.layout.projectDirectory.file("hooks/commit-msg"))
    into(rootProject.layout.projectDirectory.dir(".git/hooks"))

    filePermissions {
        unix("555")
    }

    onlyIf {
        val hookFile = File(inputs.properties["targetHookPath"] as String)

        if (hookFile.exists()) {
            println("\uD83D\uDFE2 commit-msg git hook already installed.")
            false
        } else {
            println("\uD83D\uDD04 Installing commit-msg git hook...")
            true
        }
    }

    doLast {
        val hooksDir = File(inputs.properties["gitHooksDirPath"] as String)
        val commitMsg = File(inputs.properties["targetHookPath"] as String)

        require(value = hooksDir.exists()) {
            "\uD83D\uDD34 Git hooks directory does not exist: $hooksDir"
        }

        require(value = commitMsg.exists()) {
            "\uD83D\uDD34 commit-msg git hook was not installed. Expected at: $commitMsg"
        }

        if (!commitMsg.canExecute()) commitMsg.setExecutable(true)

        println("\uD83D\uDFE2 commit-msg git hook installed successfully.")
        println("\uD83D\uDCCC Location: $hooksDir")

        hooksDir
            .listFiles()
            ?.filter { file -> file.isFile }
            ?.sortedBy { file -> file.name }
            ?.find { hook ->
                hook.name == "commit-msg"
            }?.let { hook ->
                val isExecutable =
                    if (hook.canExecute()) {
                        "✅"
                    } else {
                        "❌"
                    }
                println("\uD83D\uDCDD ${hook.name}: isExecutable = $isExecutable")
            }
    }
}

tasks
    .matching { task ->
        task.name == "assembleDebug"
    }
    .configureEach {
        dependsOn(installPreCommitGitHook)
        dependsOn(installCheckCommitMessageGitHook)
    }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}

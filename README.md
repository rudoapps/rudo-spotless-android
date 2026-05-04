<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

# Table of contents

- [Rudo Spotless v1.0.0 🪶](#rudo-spotless-v100-)
- [A lightweight and customizable Kotlin linter for Gradle](#a-lightweight-and-customizable-kotlin-linter-for-gradle)
- [What does this plugin do?](#what-does-this-plugin-do)
- [System Requirements](#system-requirements)
- [Notes](#notes)
- [Publishing a release to Maven Central](#publishing-a-release-to-maven-central)
  - [Requirements](#requirements)
    - [Local GPG configuration](#local-gpg-configuration)
  - [1. Update the version](#1-update-the-version)
  - [2. Generate the main plugin JAR](#2-generate-the-main-plugin-jar)
  - [3. Generate the Maven staging repository](#3-generate-the-maven-staging-repository)
  - [4. Verify the generated files](#4-verify-the-generated-files)
  - [5. Create the ZIP bundle](#5-create-the-zip-bundle)
  - [6. Upload to Maven Central](#6-upload-to-maven-central)
  - [Release command summary](#release-command-summary)
  - [Congratulations 👏👏👏](#congratulations-)
- [Usage in local](#usage-in-local)
  - [1. Get the project](#1-get-the-project)
  - [2. Publish the plugin to mavenLocal](#2-publish-the-plugin-to-mavenlocal)
  - [3. Use the Gradle plugin in your project](#3-use-the-gradle-plugin-in-your-project)
    - [3.1. Enable mavenLocal](#31-enable-mavenlocal)
    - [3.2. Add the plugin version](#32-add-the-plugin-version)
    - [3.3. Apply the plugin](#33-apply-the-plugin)
    - [3.3. Configure maven to deploy in local](#33-configure-maven-to-deploy-in-local)
  - [4. Run Rudo Spotless manually (optional)](#4-run-rudo-spotless-manually-optional)
  - [5. Example: run Rudo Spotless via a Git pre-commit hook](#5-example-run-rudo-spotless-via-a-git-pre-commit-hook)
    - [5.1. Create the pre-commit hook](#51-create-the-pre-commit-hook)
    - [5.2. Make it executable](#52-make-it-executable)
  - [Congratulations 👏👏👏](#congratulations--1)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

---

# Rudo Spotless v1.0.0 🪶

## A lightweight and customizable Kotlin linter for Gradle

**Rudo Spotless 🪶** is a lightweight, reliable, and customizable Gradle plugin for formatting and linting Kotlin code.  
It is developed by **Rudo** and is based on **Spotless** to ensure a clean and consistent code style.

---

# What does this plugin do?

➪ Formats Kotlin code using Spotless.  
➪ Enforces a consistent code style.  
➪ Integrates seamlessly with Gradle.  
➪ Can be executed manually or as part of a CI pipeline.

---

# System Requirements

☕️ **JDK:** 21 or higher.  
🐘 **Gradle:** 9.x or higher.  
🅺 **Kotlin:** Compatible with Kotlin projects.

---

# Notes

This repository contains two modules:

➢ **app**: a showcase/sample Android app that applies and demonstrates the plugin in a real project.  
➢ **rudo-spotless**: the actual Gradle plugin module that provides **Rudo Spotless**.

The `app` module is included only as an example; the plugin code lives in `rudo-spotless`.

---

# Publishing a release to Maven Central

This project is published as a Gradle plugin. The Gradle publishing configuration is already included in the project, so release maintainers only need to prepare their local environment, update the version, generate the staging bundle, and upload it to Maven Central.

---

## Requirements

Before publishing, make sure you have:

- JDK 21 or newer.
- GPG installed and configured locally.
- A GPG key available in your local keyring.
- The GPG public key uploaded to a public key server, such as `keyserver.ubuntu.com`.
- Access to the Maven Central namespace used by this project.
- Access to Sonatype Central Portal.

Each maintainer can use their own GPG key. Do not share private GPG keys.

---

### Local GPG configuration

Each maintainer must configure their local Gradle properties file:

```text
~/.gradle/gradle.properties
```

Example:  

```text
# GPG signing configuration
signing.gnupg.executable=gpg
signing.gnupg.keyName=YOUR_GPG_KEY_ID
```

If gpg is not available in the default path, use the full path:

```text
signing.gnupg.executable=/opt/homebrew/bin/gpg
signing.gnupg.keyName=YOUR_GPG_KEY_ID
```

You can find the key ID with:

```bash
gpg --list-secret-keys --keyid-format=long
```

The public key must be available on the key server:

```bash
gpg --keyserver hkps://keyserver.ubuntu.com --send-keys YOUR_GPG_KEY_ID
```

---

## 1. Update the version

Update the project version before publishing:

```kotlin
version = "1.0.1"
```

**Do not reuse a version that has already been published.**

---

## 2. Generate the main plugin JAR

Run in terminal:

```bash
./gradlew :rudo-spotless:clean
./gradlew :rudo-spotless:jar
```

Check that the main JAR exists:

```bash
ls -la rudo-spotless/build/libs
```

Expected files include:

```text
rudo-spotless-<version>.jar
rudo-spotless-<version>-sources.jar
rudo-spotless-<version>-javadoc.jar
```

---

## 3. Generate the Maven staging repository

Run in terminal:

```bash
./gradlew :rudo-spotless:publishAllPublicationsToMavenCentralBundleRepository
```

The staging repository will be generated at:

```text
rudo-spotless/build/maven-central-bundle
```

---

## 4. Verify the generated files

Check that the generated staging repository contains POM files:

```bash
find rudo-spotless/build/maven-central-bundle -name "*.pom"
```

Check that signature files were generated:

```bash
find rudo-spotless/build/maven-central-bundle -name "*.asc"
```

Check that the Gradle plugin marker exists:

```bash
find rudo-spotless/build/maven-central-bundle -path "*es.rudo.spotless.gradle.plugin*"
```

The staging repository should contain both:

```text
es/rudo/rudo-spotless/<version>/...
es/rudo/spotless/es.rudo.spotless.gradle.plugin/<version>/...
```

The second path is the Gradle plugin marker artifact. It is required so the plugin can be applied using:

```kotlin
plugins {
    ...
    id("es.rudo.spotless")
}
```

---

## 5. Create the ZIP bundle

From inside the staging directory, create the ZIP:

```bash
cd rudo-spotless/build/maven-central-bundle
zip -r rudo-spotless-<version>.zip .
```

Example:

```bash
zip -r rudo-spotless-1.0.1.zip .
```

Verify the ZIP structure:

```bash
unzip -l rudo-spotless-<version>.zip | head -40
```

The ZIP must contain the Maven layout directly:

```text
es/rudo/...
```

It must not contain an extra parent folder like:

```text
maven-central-bundle/es/rudo/...
```

---

## 6. Upload to Maven Central

Open Sonatype Central Portal and go to:

```text
Publish > Deployments
```

Upload the generated ZIP:

```text
rudo-spotless-<version>.zip
```

Wait for validation.  
If validation succeeds, publish the deployment.  
If validation fails, check the error details in the portal. Common causes are:  
- Missing or invalid GPG signatures.
- Public GPG key not found.
- Missing `sources.jar`.
- Missing `javadoc.jar`.
- Invalid POM metadata.
- Namespace permissions issue.
- Incorrect ZIP structure.

---

## Release command summary

```bash
./gradlew :rudo-spotless:clean
./gradlew :rudo-spotless:jar
./gradlew :rudo-spotless:publishAllPublicationsToMavenCentralBundleRepository

cd rudo-spotless/build/maven-central-bundle
zip -r rudo-spotless-<version>.zip .
```

---

## Congratulations 👏👏👏

🎉 Congratulations! Rudo Spotless 🪶 has been successfully published to Maven and is now ready to use in your projects.

---

# Usage in local

The following sections describe how to set up and use the plugin in local.  

---

## 1. Get the project

First, download or clone this repository to your computer.

Then, open the project in Android Studio (or IntelliJ IDEA) and let Gradle finish syncing.

---

## 2. Publish the plugin to mavenLocal

To use the plugin locally, publish it to `mavenLocal`.

From the root of the previously downloaded project, run:

```bash
./gradlew :rudo-spotless:publishToMavenLocal
```

---

## 3. Use the Gradle plugin in your project

### 3.1. Enable mavenLocal
Ensure that `mavenLocal()` is included in your `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        ...
    }
}
```

### 3.2. Add the plugin version
Edit your `libs.versions.toml` file and add the following entries.

Inside the **[versions]** block:
```toml
rudo-spotless = "1.0.0"
```

Inside the **[plugins]** block:
```toml
es-rudo-spotless = { id = "es.rudo.spotless", version.ref = "rudo-spotless" }
```

### 3.3. Apply the plugin
In your `build.gradle.kts` (app module), apply the plugin:
```kotlin
plugins {
    ...
    alias(libs.plugins.es.rudo.spotless)
}
```

### 3.3. Configure maven to deploy in local
In your `build.gradle.kts` (rudo-spotless module), add the following code:
```kotlin
publishing {
    ...
    publications {
        create<MavenPublication>("rudoSpotless") {
            from(components["java"])
            groupId = "es.rudo.spotless"
            artifactId = "rudo-spotless-gradle-plugin"
            version = "1.0.0"
        }
    }
}
```

---

## 4. Run Rudo Spotless manually (optional)
To format all Kotlin files in your project, run:

```bash
./gradlew spotlessApply --no-configuration-cache
```

To verify formatting without modifying files, run:
```bash
./gradlew spotlessCheck
```

---

## 5. Example: run Rudo Spotless via a Git pre-commit hook

You can integrate **Rudo Spotless** into your workflow by running `spotlessApply` automatically before each commit.  
This ensures code is formatted consistently and prevents committing directly to protected branches.

### 5.1. Create the pre-commit hook

Create a file at `.git/hooks/pre-commit` and paste the following script:

```sh
#!/bin/sh

ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$ROOT_DIR" || exit 0

BRANCH_NAME="$(git rev-parse --abbrev-ref HEAD)"

if echo "$BRANCH_NAME" | grep -Eq '^(main|develop)$'; then
  echo "❌ You cannot commit directly to the '$BRANCH_NAME' branch."
  echo "Create a feature/, bugfix/, or hotfix/ branch and commit there."
  exit 1
fi

if echo "$BRANCH_NAME" | grep -Eq '^(feature|bugfix|hotfix)/'; then
  BRANCH_PATTERN='^(feature|bugfix|hotfix)/[A-Z][A-Z0-9_-]*-(X|[0-9]+)-[a-z0-9]+(-[a-z0-9]+)*$'

  if echo "$BRANCH_NAME" | grep -Eq "$BRANCH_PATTERN"; then
    echo "✅ Branch name format is valid: $BRANCH_NAME."
  else
    echo "❌ Invalid branch name: $BRANCH_NAME."
    echo
    echo "It must follow this format:"
    echo "feature/PROJECT-123-add-login"
    echo "feature/PROJECT-123-change-repository-format"
    echo "bugfix/PROJECT-123-change-develop-endpoint"
    echo "bugfix/PROJECT-X-add-new-endpoint"
    echo
    exit 1
  fi
else
  echo "ℹ️ Branch '$BRANCH_NAME' is not feature/bugfix/hotfix, skipping validation."
fi

echo "🔁 Running spotlessApply..."

if ./gradlew spotlessApply --no-configuration-cache; then
  echo "✅ spotlessApply OK"
  git add -u
  exit 0
else
  echo "❌ spotlessApply failed (check lint errors)"
  exit 1
fi
```

### 5.2. Make it executable

Run this command in your terminal:

```bash
chmod +x .git/hooks/pre-commit
```

From now on, every commit will automatically run `spotlessApply`.  
If formatting changes are applied, the hook will re-stage the modified files before completing the commit.

---

## Congratulations 👏👏👏
You are now ready to use Rudo Spotless 🪶 in your project.

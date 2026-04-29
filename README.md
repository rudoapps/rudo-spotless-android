# Rudo Spotless v1.0.0 🪶

### A lightweight and customizable Kotlin linter for Gradle

**Rudo Spotless 🪶** is a lightweight, reliable, and customizable Gradle plugin for formatting and linting Kotlin code.  
It is developed by **Rudo** and is based on **Spotless** to ensure a clean and consistent code style.

---

## What does this plugin do?

➪ Formats Kotlin code using Spotless.  
➪ Enforces a consistent code style.  
➪ Integrates seamlessly with Gradle.  
➪ Can be executed manually or as part of a CI pipeline.

---

## System Requirements

☕️ **JDK:** 17 or higher (recommended: 21).  
🐘 **Gradle:** 8.x or higher.  
🅺 **Kotlin:** Compatible with Kotlin projects.

---

## Notes

This repository contains two modules:

➢ **app**: a showcase/sample Android app that applies and demonstrates the plugin in a real project.  
➢ **rudo-spotless**: the actual Gradle plugin module that provides **Rudo Spotless**.

The `app` module is included only as an example; the plugin code lives in `rudo-spotless`.

---


## Usage

The following sections describe how to set up and use the plugin.

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

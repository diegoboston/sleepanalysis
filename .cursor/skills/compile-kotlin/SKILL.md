---
name: compile-kotlin
description: >-
  Fast Kotlin-only verify (compile + JVM unit tests, no APK). Prefer rebuild-app
  after any app change. Use compile-kotlin only when the user explicitly wants
  a quicker check without assembleDebug, or when editing compile-kotlin itself.
---

# Compile Kotlin

**For task completion after app changes, use [rebuild-app](../rebuild-app/SKILL.md)**
instead — it also assembles the debug APK.

This skill is a **faster subset**: compile + unit tests only (no APK packaging).

CI on GitHub Actions is the default build path for this repo. Use this script
only when a local JDK 17 + Android SDK env exists at `~/tmp/android-build/env.sh`.

## Mandatory command

```bash
bash .cursor/skills/compile-kotlin/scripts/compile-kotlin.sh
```

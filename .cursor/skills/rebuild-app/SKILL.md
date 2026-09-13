---
name: rebuild-app
description: >-
  Mandatory verify step after editing this Android app when a local Android
  toolchain exists. Default for this repo is GitHub Actions CI (assembleRelease).
---

# Rebuild App

This repo is set up to **build on GitHub Actions**. Run the local script only
when `~/tmp/android-build/env.sh` exists.

## Mandatory command

```bash
bash .cursor/skills/rebuild-app/scripts/rebuild-app.sh
```

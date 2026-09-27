# Building KITSUNEnime

KITSUNEnime is a security-hardened fork of the **Dantotsu** AniList tracking client.
The Android project lives at the **repository root** (Kotlin + Gradle).

## Toolchain

The build toolchain is pinned by the project and matches CI:

| Tool | Version |
|------|---------|
| JDK | 21 (Temurin) |
| Gradle | 9.7.1 (via the wrapper) |
| Android Gradle Plugin | 9.4.0 |
| Kotlin | 2.4.20 |
| `compileSdk` | 37 → SDK platform package `platforms;android-37.0` |
| Build Tools | `build-tools;37.0.0` |
| `minSdk` / `targetSdk` | 26 / 36 |

> **Important:** Android API 37 is published **only** as a minor-versioned SDK package
> (`platforms;android-37.0`). `sdkmanager "platforms;android-37"` fails with
> *"Failed to find package"*. Install the `.0` package instead. `compileSdk = 37`
> maps to `android-37.0`.

## Local build

```bash
# One-time SDK setup (adjust to your $ANDROID_HOME)
sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-37.0" "build-tools;37.0.0"

# Gradle commands run from the repository root
./gradlew assembleGoogleDebug      # Google flavor debug APK (what CI builds)
./gradlew assembleFdroidDebug      # F-Droid flavor (no Firebase / Google services)
```

Build variants combine two flavors (`google`, `fdroid`) with three build types
(`alpha`, `debug`, `release`). Output APKs land in
`app/build/outputs/apk/<flavor>/<buildType>/`.

The `google` flavor requires `app/google-services.json` (committed for debug builds) and
pulls in Firebase; the `fdroid` flavor is fully free of Google/Firebase dependencies.

## Continuous Integration

CI is defined in [`.github/workflows/kitsune-ci.yml`](../.github/workflows/kitsune-ci.yml) at the
repository root (GitHub only runs workflows located at the repo-root `.github/workflows/`).
On every push / pull request to `main` (and on manual dispatch) it:

1. Sets up JDK 21 and Gradle.
2. Installs `platforms;android-37.0` and `build-tools;37.0.0` explicitly (too new to be
   preinstalled on the runner).
3. Runs `./gradlew assembleGoogleDebug`.
4. Uploads the resulting APK(s) as the `kitsunenime-google-debug` artifact.

## Notes for release builds

`assembleGoogleRelease` is minified (R8). AGP 9 turns on
`android.r8.strictFullModeForKeepRules` by default, so the broad `-keep` rules in
`app/proguard-rules.pro` may need review before shipping an obfuscated release. CI currently
builds the **debug** variant only and is not affected by this.

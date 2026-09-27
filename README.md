# KITSUNEnime

KITSUNEnime is a security-hardened fork of the **Dantotsu** AniList tracking client for Android.
This repository is organized as a small monorepo:

| Path | Contents |
|------|----------|
| [`dantotsu/`](./dantotsu) | The Android application (Kotlin + Gradle). All app source, Gradle config and CI live here. |
| [`docs/`](./docs) | Project documentation, security audits and RFC notes (e.g. [`EXTENSION-AUDIT-001.md`](./docs/EXTENSION-AUDIT-001.md)). |

> **Tracking tool only.** Like upstream Dantotsu, KITSUNEnime does not host, provide, distribute
> or endorse any streaming content, media, or third-party extensions. See the app's
> [README](./dantotsu/README.md) and [privacy policy](./dantotsu/privacy_policy.md) for the full terms.

## Build

The build toolchain is pinned by the project and matches the CI:

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
> (`platforms;android-37.0`). `sdkmanager "platforms;android-37"` will fail with
> *"Failed to find package"*. Install the `.0` package instead.

### Local build

```bash
# One-time SDK setup (adjust to your $ANDROID_HOME)
sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-37.0" "build-tools;37.0.0"

# All Gradle commands run from the dantotsu/ subfolder
cd dantotsu

# Debug APK (Google flavor – what CI builds)
./gradlew assembleGoogleDebug

# F-Droid flavor (no Firebase / Google services)
./gradlew assembleFdroidDebug
```

Build variants combine two flavors (`google`, `fdroid`) with three build types
(`alpha`, `debug`, `release`). Output APKs land in
`dantotsu/app/build/outputs/apk/<flavor>/<buildType>/`.

The `google` flavor requires `dantotsu/app/google-services.json` (already committed for debug builds)
and pulls in Firebase; the `fdroid` flavor is fully free of Google/Firebase dependencies.

## Continuous Integration

CI is defined in [`dantotsu/.github/workflows/kitsune-ci.yml`](./dantotsu/.github/workflows/kitsune-ci.yml).
On every push / pull request to `main` (and on manual dispatch) it:

1. Sets up JDK 21 and Gradle.
2. Installs `platforms;android-37.0` and `build-tools;37.0.0` explicitly (they are too new to be
   preinstalled on the runner).
3. Runs `./gradlew assembleGoogleDebug`.
4. Uploads the resulting APK(s) as the `kitsunenime-google-debug` artifact.

The remaining workflows in `dantotsu/.github/workflows/` handle issue/PR greetings and
extension-issue triage.

## Security posture

KITSUNEnime treats the Aniyomi/Dantotsu extension subsystem as an **untrusted compatibility
boundary**. See [`docs/EXTENSION-AUDIT-001.md`](./docs/EXTENSION-AUDIT-001.md) for the current audit,
open hardening tasks and the RFC decisions that gate "compatibility mode".

## License

See [`dantotsu/LICENSE.md`](./dantotsu/LICENSE.md). KITSUNEnime inherits the upstream Dantotsu license.

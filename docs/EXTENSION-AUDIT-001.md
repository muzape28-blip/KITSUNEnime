# Extension Audit 001

- **Project:** KITSUNEnime
- **Upstream snapshot:** commit `70358c75985724ff3cc1dad3dff2b3241e35872c`
- **Scope:** Dantotsu/An iyomi extension compatibility subsystem
- **Status:** Preliminary source audit
- **Runtime/build status:** Not yet verified

## Findings

### Existing capabilities

- Custom repository input exists in `settings/AddRepositoryBottomSheet.kt`.
- Repository metadata is fetched through `ExtensionRepoMetaHelper`.
- Anime, manga, and novel extension repositories are stored separately.
- Aniyomi anime extension types are consumed by `parsers/AniyomiAdapter.kt`.
- Dynamic anime parsers expose source language, NSFW flag, dubbed/subbed selection, episode loading, seasons, source URLs, subtitles, and video tracks.
- Extension installation flow exists through the upstream extension manager and `InstallerSteps`.
- Installed extension UI and extension test UI exist.
- Addon loading uses Android package discovery and `PathClassLoader`.

### Critical observations

1. `AddRepositoryBottomSheet` accepts HTTP as well as HTTPS and converts shorthand input into raw GitHub URLs. This does not satisfy RFC-0002 HTTPS-only policy.
2. Repository validation is mostly filename/shape validation. It does not prove publisher identity, schema trust, APK provenance, or signature continuity.
3. `InstallerSteps` reports installation errors to Crashlytics and logs throwable details. This requires review for URL, token, cookie, or source leakage.
4. `AddonLoader` loads installed code using `PathClassLoader`; this is dynamic third-party code in the host trust boundary, not a full sandbox.
5. The current code checks package identity but the audit has not yet established certificate pinning, SHA-256 verification, or publisher trust.
6. The upstream app includes package installation/update permissions in the manifest. These must not be inherited blindly.
7. Dynamic source data includes an NSFW flag, but RFC-0001 requires richer content labels and user controls.
8. Aniyomi compatibility is broad and feature-rich, but source legality, availability, and security are not proven by extension metadata.

## Decision mapping

| Area | Current upstream state | RFC-0002 result |
|---|---|---|
| Custom repo URL | Implemented | Keep, harden |
| HTTPS-only | Not satisfied | Must change |
| Schema validation | Partial/unknown | Add guard |
| Manual install | Existing upstream flow | Keep, make explicit |
| Auto-install/update | Requires audit | Disable for KITSUNEnime |
| Hash/signature verification | Not proven | Add before trusted install |
| Extension loading | PathClassLoader | Compatibility-only, clearly untrusted |
| Disable/uninstall | Existing manager likely supports it | Verify with regression test |
| Core without extension | Expected but not yet tested | Must test |
| Download separation | Not yet enforced by this audit | Must separate capability |
| Sensitive logging | Needs review | Redact before release |

## Immediate changes before enabling compatibility mode

1. Reject non-HTTPS repository URLs.
2. Reject unsupported schemes and malformed redirects.
3. Add bounded response size and timeout handling.
4. Validate repository index schema before displaying install actions.
5. Display repository URL, publisher, package, version, language, capabilities, NSFW label, and trust status.
6. Add explicit install consent.
7. Add package certificate/hash inspection before installation.
8. Detect certificate change and show a blocking or high-friction warning.
9. Disable automatic extension update/install behavior.
10. Redact exceptions and URLs before Crashlytics/logging.
11. Remove package-management permissions unless a later decision explicitly requires them.
12. Test extension removal without deleting library/progress.
13. Add tests for invalid repository, duplicate package, malformed index, hash mismatch, certificate change, and network timeout.

## Not yet concluded

- Exact upstream extension-manager call chain.
- Whether Dantotsu verifies extension signatures internally.
- Complete package permission behavior.
- Whether installed extensions run in the same process or only share app-level capability through Android package boundaries.
- Full download capability separation.
- Runtime behavior on Infinix SMART 9 HD.

## Honest status

The subsystem is a strong compatibility foundation, but it is **not RFC-compliant yet**. It should be reused behind a hardened compatibility boundary, not exposed unchanged in a production KITSUNEnime build.

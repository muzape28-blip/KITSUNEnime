package ani.dantotsu.security

import java.net.URI

/**
 * Boundary policy for user-provided extension repositories.
 *
 * This is intentionally small and side-effect free so it can be unit-tested
 * before it is connected to the upstream extension installer.
 */
object ExtensionTrustPolicy {
    private val allowedSchemes = setOf("https")
    private const val maxRepositoryUrlLength = 2048

    fun validateRepositoryUrl(raw: String): Validation {
        val value = raw.trim()
        if (value.isEmpty()) return Validation.Rejected("Repository URL tidak boleh kosong")
        if (value.length > maxRepositoryUrlLength) {
            return Validation.Rejected("Repository URL terlalu panjang")
        }

        val uri = runCatching { URI(value) }.getOrElse {
            return Validation.Rejected("Repository URL tidak valid")
        }

        val scheme = uri.scheme?.lowercase()
        if (scheme !in allowedSchemes) {
            return Validation.Rejected("Repository harus menggunakan HTTPS")
        }
        if (uri.userInfo != null) {
            return Validation.Rejected("Credential tidak boleh ada di URL repository")
        }
        if (uri.host.isNullOrBlank()) {
            return Validation.Rejected("Repository harus memiliki host yang valid")
        }
        if (uri.fragment != null) {
            return Validation.Rejected("Fragment URL tidak didukung")
        }

        return Validation.Accepted(value)
    }

    sealed interface Validation {
        data class Accepted(val normalizedUrl: String) : Validation
        data class Rejected(val reason: String) : Validation
    }
}

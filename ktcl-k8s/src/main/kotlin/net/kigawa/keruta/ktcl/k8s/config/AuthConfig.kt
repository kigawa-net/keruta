package net.kigawa.keruta.ktcl.k8s.config

import net.kigawa.keruta.ktcp.domain.auth.key.PemKey

data class AuthConfig(
    val privateKey: PemKey,
) {
    companion object {
        fun load(): AuthConfig = AuthConfig(
            privateKey = System.getenv("PRIVATE_KEY")
                ?: throw IllegalStateException("PRIVATE_KEY is required"),

        )
    }
}

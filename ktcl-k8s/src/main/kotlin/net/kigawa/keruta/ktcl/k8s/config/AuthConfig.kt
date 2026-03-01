package net.kigawa.keruta.ktcl.k8s.config

import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey

data class AuthConfig(
    val privateKey: PrivateKey,
) {
    companion object {
        fun load(): AuthConfig {
            return AuthConfig(
                privateKey = PrivateKey(
                    System.getenv("PRIVATE_KEY")
                        ?: throw IllegalStateException("PRIVATE_KEY is required"),
                )
            )
        }
    }
}

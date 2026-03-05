package net.kigawa.keruta.ktcl.k8s.config

import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey

data class AuthConfig(
    val privateKey: KerutaPrivateKey,
) {
    companion object {
        fun load(): AuthConfig {
            return AuthConfig(
                privateKey = KerutaPrivateKey(
                    System.getenv("PRIVATE_KEY")
                        ?: throw IllegalStateException("PRIVATE_KEY is required"),
                )
            )
        }
    }
}

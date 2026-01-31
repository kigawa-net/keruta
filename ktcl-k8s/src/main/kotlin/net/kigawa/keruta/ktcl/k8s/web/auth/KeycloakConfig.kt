package net.kigawa.keruta.ktcl.k8s.web.auth

data class KeycloakConfig(
    val audience: String,
    val jwksUrl: String,
    val issuer: String
)

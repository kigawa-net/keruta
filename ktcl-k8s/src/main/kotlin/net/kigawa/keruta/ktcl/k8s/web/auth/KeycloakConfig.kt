package net.kigawa.keruta.ktcl.k8s.web.auth

import java.net.URI

data class KeycloakConfig(
    val audience: String,
    val jwksUrl: String,
    val issuer: URI,
    val authorizationEndpoint: String,
)

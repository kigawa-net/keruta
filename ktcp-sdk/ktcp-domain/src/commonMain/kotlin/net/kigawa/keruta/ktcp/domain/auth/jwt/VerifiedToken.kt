package net.kigawa.keruta.ktcp.domain.auth.jwt

interface VerifiedToken {
    val audience: String
    val issuer: Issuer
    val subject: String
}

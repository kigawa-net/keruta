package net.kigawa.keruta.ktcp.model.auth.jwt

interface VerifiedToken {
    val audience: String
    val issuer: Issuer
    val subject: String
}

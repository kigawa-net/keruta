package net.kigawa.keruta.ktcp.server.auth

data class IdpConfig(
    val issuer: String,
    val audience: String,
) {
    fun asIdp(subject: String): Idp {
        return Idp(audience, subject, issuer)
    }
}

package net.kigawa.keruta.ktcp.server.auth

data class ProviderIdpConfig(
    val issuer: String,
    val audience: String,
    val name: String,
) {
    fun asIdp(subject: String): Idp {
        return Idp(audience, subject, issuer)
    }
}

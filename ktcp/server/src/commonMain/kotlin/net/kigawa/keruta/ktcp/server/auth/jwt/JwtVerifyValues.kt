package net.kigawa.keruta.ktcp.server.auth.jwt

import net.kigawa.kodel.api.net.Url

class JwtVerifyValues(
    val issuer: Url,
    val audience: String,
    val subject: String,
)

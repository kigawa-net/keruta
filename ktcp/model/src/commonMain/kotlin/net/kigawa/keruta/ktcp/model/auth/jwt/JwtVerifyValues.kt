package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.kodel.api.net.Url

class JwtVerifyValues(
    val issuer: Url,
    val audience: String,
    val subject: String,
)

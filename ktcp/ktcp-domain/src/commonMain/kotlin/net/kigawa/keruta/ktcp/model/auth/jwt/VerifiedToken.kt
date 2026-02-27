package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.kodel.api.net.Url

interface VerifiedToken {
    val audience: String
    val issuer: Url
    val subject: String
}

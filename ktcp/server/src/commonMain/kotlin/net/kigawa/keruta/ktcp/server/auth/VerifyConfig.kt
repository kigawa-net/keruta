package net.kigawa.keruta.ktcp.server.auth

interface VerifyConfig {
    val issuer: String
    val jwksUrl: String?
    val audience: String
}

package net.kigawa.keruta.ktcp.server.auth

data class Verified(
    val user: VerifiedToken,
    val server: VerifiedToken,
)

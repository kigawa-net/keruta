package net.kigawa.keruta.kicp.domain.err

sealed class KicpErr(message: String, cause: Throwable? = null) : Exception(message, cause)

class JwksFetchErr(url: String, cause: Throwable? = null) :
    KicpErr("Failed to fetch JWKS from $url", cause)

class TokenVerificationErr(message: String, cause: Throwable? = null) :
    KicpErr(message, cause)

class RegisterTokenNotFoundErr :
    KicpErr("Register token not found or already used")

class RegisterTokenExpiredErr :
    KicpErr("Register token has expired")

class PeerVerificationErr(message: String, cause: Throwable? = null) :
    KicpErr(message, cause)

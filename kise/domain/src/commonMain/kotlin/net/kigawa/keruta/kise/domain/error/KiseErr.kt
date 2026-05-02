package net.kigawa.keruta.kise.domain.error

/**
 * kiseモジュールのエラー型階層
 */
sealed class KiseErr(message: String, cause: Throwable? = null) : Exception(message, cause)

class InvalidTokenErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class TokenExpiredErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class ProviderNotFoundErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class ProviderMismatchErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class SessionNotFoundErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class SessionExpiredErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class UserNotFoundErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class UserAlreadyExistsErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class JwksFetchErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class JwtCreateErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)

class DatabaseErr(message: String, cause: Throwable? = null) : KiseErr(message, cause)
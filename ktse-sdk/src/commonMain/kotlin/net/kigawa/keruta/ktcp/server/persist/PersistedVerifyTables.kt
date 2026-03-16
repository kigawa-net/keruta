package net.kigawa.keruta.ktcp.server.persist

data class PersistedVerifyTables(
    val user: PersistedUser,
    val userIdp: PersistedUserIdp,
    val provider: PersistedProvider,
)

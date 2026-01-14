package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.auth.UserIdp

interface PersistedUserIdp {
    val userId: Long
    val subject: String

    fun asUserIdp(): UserIdp
}

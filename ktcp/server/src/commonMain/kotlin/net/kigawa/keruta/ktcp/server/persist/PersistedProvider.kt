package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.auth.UserIdp

interface PersistedProvider {
    fun asUserIdp(subject: String): UserIdp
}

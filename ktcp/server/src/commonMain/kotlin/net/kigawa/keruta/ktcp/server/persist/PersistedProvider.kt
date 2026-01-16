package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.auth.Idp

interface PersistedProvider {
    fun asUserIdp(subject: String): Idp
}

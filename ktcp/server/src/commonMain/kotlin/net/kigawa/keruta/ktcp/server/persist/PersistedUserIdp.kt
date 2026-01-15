package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.auth.Idp

interface PersistedUserIdp {
    val userId: Long
    val subject: String

    fun asUserIdp(): Idp
}

package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.kodel.api.net.Url

interface PersistedUserIdp {
    val audience: String
    val issuer: Url
    val userId: Long
    val subject: String

}

package net.kigawa.keruta.ktcp.server.persist

interface PersistedUser {

    val currentIdp: PersistedUserIdp
    val id: Long
}

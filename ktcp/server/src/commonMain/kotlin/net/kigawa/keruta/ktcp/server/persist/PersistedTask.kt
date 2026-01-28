package net.kigawa.keruta.ktcp.server.persist

interface PersistedTask {

    val id: Long
    val title: String
    val description: String
}

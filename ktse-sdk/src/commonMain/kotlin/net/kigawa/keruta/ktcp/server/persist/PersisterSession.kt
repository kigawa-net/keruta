package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister

interface PersisterSession {
    val verifyTablesPersister: VerifyTablesPersister
    fun auth(persistedVerifyTables: PersistedVerifyTables): AuthenticatedPersisterSession
}

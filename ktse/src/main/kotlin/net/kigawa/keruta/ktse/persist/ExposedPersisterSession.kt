package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktcp.server.persist.PersisterSession
import net.kigawa.keruta.ktse.persist.accessor.ExposedVerifyTablesPersister
import net.kigawa.keruta.ktse.persist.db.DbPersister

class ExposedPersisterSession(
    val dbPersister: DbPersister,
): PersisterSession {
    override val verifyTablesPersister: VerifyTablesPersister = ExposedVerifyTablesPersister(dbPersister)

    override fun auth(
        persistedVerifyTables: PersistedVerifyTables,
    ): AuthenticatedPersisterSession = ExposedAuthedPersisterSession(
        persistedVerifyTables, dbPersister
    )
}

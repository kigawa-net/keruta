package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.queue.QueueToCreate
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedQueue
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.insert

class DbQueuePersisterDsl(
    val transaction: org.jetbrains.exposed.sql.Transaction,
) {
    fun createQueue(queueToCreate: QueueToCreate, provider: PersistedProvider): Res<PersistedQueue, KtcpErr> =
        transaction.run {
            val queue = QueueTable.insert {
                it[QueueTable.name] = queueToCreate.name
                it[QueueTable.providerId] = provider.id
            }.resultedValues?.singleOrNull() ?: return Res.Err(NoSingleRecordErr("", null))
            return Res.Ok(ExposedPersistedQueue(queue))
        }

}

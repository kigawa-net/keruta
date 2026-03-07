package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedQueue(queue: ResultRow): PersistedQueue {
    override val id: Long = queue[QueueTable.id]
    override val name: String = queue[QueueTable.name]
    override val providerId: Long = queue[QueueTable.providerId]
    override val setting: String = queue[QueueTable.setting]
}

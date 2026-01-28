package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedTask(queue: ResultRow): PersistedTask {
    override val id: Long = queue[QueueTable.id]
    override val name: String = queue[QueueTable.name]

}

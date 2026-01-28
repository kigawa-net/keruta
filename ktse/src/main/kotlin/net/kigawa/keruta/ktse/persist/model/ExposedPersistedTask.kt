package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import net.kigawa.keruta.ktse.persist.db.table.TaskTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedTask(queue: ResultRow): PersistedTask {
    override val id: Long = queue[TaskTable.id]
    override val title: String = queue[TaskTable.title]
    override val description: String = queue[TaskTable.description]

}

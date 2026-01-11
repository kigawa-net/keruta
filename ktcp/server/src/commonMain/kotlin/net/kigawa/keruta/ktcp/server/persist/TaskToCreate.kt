package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg

class TaskToCreate {
    companion object {
        fun from(input: ServerTaskCreateArg): TaskToCreate {
            return TaskToCreate()
        }
    }
}

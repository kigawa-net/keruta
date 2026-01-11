package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg

data class TaskToCreate(
    val name: String,

    ) {
    companion object {
        fun from(input: ServerTaskCreateArg): TaskToCreate {
            return TaskToCreate(
                input.taskCreateMsg.name
            )
        }
    }
}

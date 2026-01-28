package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg

data class TaskToCreate(
    val name: String,
    val queueId: String,
) {
    companion object {
        fun from(input: ServerTaskCreateArg): TaskToCreate {
            return TaskToCreate(
                input.taskCreateMsg.name,
                input.taskCreateMsg.queueId
            )
        }
    }
}

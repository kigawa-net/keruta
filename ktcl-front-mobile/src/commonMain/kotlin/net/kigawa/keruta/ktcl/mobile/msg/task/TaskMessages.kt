package net.kigawa.keruta.ktcl.mobile.msg.task

import kotlinx.serialization.Serializable

@Serializable
data class ServerTaskCreateMsg(
    val type: String = "task_create",
    val queueId: Long,
    val title: String,
    val description: String,
)

@Serializable
data class ServerTaskListMsg(
    val type: String = "task_list",
    val queueId: Long,
)

@Serializable
data class ClientTaskCreatedMsg(
    val type: String = "task_created",
    val id: Long,
)

@Serializable
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val status: String,
)

@Serializable
data class ClientTaskListedMsg(
    val type: String = "task_listed",
    val tasks: List<Task>,
)

@Serializable
data class ClientTaskUpdatedMsg(
    val type: String = "task_updated",
    val id: Long,
    val status: String,
)

@Serializable
data class ClientTaskMovedMsg(
    val type: String = "task_moved",
    val taskId: Long,
    val queueId: Long,
)

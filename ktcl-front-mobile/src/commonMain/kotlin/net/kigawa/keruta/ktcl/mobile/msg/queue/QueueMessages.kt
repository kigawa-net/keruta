package net.kigawa.keruta.ktcl.mobile.msg.queue

import kotlinx.serialization.Serializable

@Serializable
data class ServerQueueCreateMsg(
    val type: String = "queue_create",
    val providerId: Long,
    val name: String,
)

@Serializable
data class ServerQueueListMsg(
    val type: String = "queue_list",
)

@Serializable
data class ClientQueueCreatedMsg(
    val type: String = "queue_created",
    val queueId: Long,
)

@Serializable
data class Queue(
    val id: Long,
    val name: String,
)

@Serializable
data class ClientQueueListedMsg(
    val type: String = "queue_listed",
    val queues: List<Queue>,
)

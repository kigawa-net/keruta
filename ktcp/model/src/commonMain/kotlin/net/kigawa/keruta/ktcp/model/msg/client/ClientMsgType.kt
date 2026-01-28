package net.kigawa.keruta.ktcp.model.msg.client

import kotlinx.serialization.Serializable

@Serializable(ClientMsgTypeSerializer::class)
enum class ClientMsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    PROVIDER_LISTED("provider_listed"),
    QUEUE_CREATED("queue_created"),
    QUEUE_LISTED("queue_listed"),
    QUEUE_SHOWED("queue_showed"),
    TASK_CREATED("task_created"),
    TASK_UPDATED("task_updated"),
    TASK_MOVED("task_moved"),
    TASK_LISTED("task_listed"),
    TASK_SHOWED("task_showed"),
    ;

    companion object {
        fun fromString(decodeString: String): ClientMsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

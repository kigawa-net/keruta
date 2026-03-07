package net.kigawa.keruta.ktcp.domain.msg.client

import kotlinx.serialization.Serializable

@Serializable(ClientMsgTypeSerializer::class)
enum class ClientMsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    PROVIDER_LISTED("provider_listed"),
    PROVIDER_TOKEN_ISSUED("provider_token_issued"),
    PROVIDER_ADD_TOKEN_ISSUED("provider_add_token_issued"),
    PROVIDER_IDP_ADDED("provider_idp_added"),
    PROVIDER_DELETED("provider_deleted"),
    QUEUE_CREATED("queue_created"),
    QUEUE_LISTED("queue_listed"),
    QUEUE_SHOWED("queue_showed"),
    QUEUE_UPDATED("queue_updated"),
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

package net.kigawa.keruta.ktcp.model.msg.server

import kotlinx.serialization.Serializable

@Serializable(ServerMsgTypeSerializer::class)
enum class ServerMsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    AUTH_REQUEST("auth_request"),
    AUTH_SUCCESS("auth_success"),
    TASK_CREATE("task_create"),
    TASK_UPDATE("task_update"),
    TASK_MOVE("task_move"),
    PROVIDER_LIST("provider_list"),
    PROVIDER_ISSUE_TOKEN("provider_issue_token"),
    PROVIDER_ADD("provider_add"),
    PROVIDER_COMPLETE("provider_complete"),
    QUEUE_CREATE("queue_create"),
    QUEUE_LIST("queue_list"),
    QUEUE_SHOW("queue_show"),
    TASK_LIST("task_list"),
    TASK_SHOW("task_show"),
    ;

    companion object {
        fun fromString(decodeString: String): ServerMsgType {
            return entries.singleOrNull { it.str == decodeString }
                ?: throw IllegalArgumentException("unknown msg type $decodeString")
        }
    }
}

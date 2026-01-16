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
    PROVIDERS_REQUEST("providers_request"),
    ;

    companion object {
        fun fromString(decodeString: String): ServerMsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

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
    ;

    companion object {
        fun fromString(decodeString: String): ClientMsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

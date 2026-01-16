package net.kigawa.keruta.ktcp.model.msg.client

import kotlinx.serialization.Serializable

@Serializable(ClientMsgTypeSerializer::class)
enum class ClientMsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    PROVIDER_LIST("provider_list"),
    ;

    companion object {
        fun fromString(decodeString: String): ClientMsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.Serializable

@Serializable(ClientMsgTypeSerializer::class)
enum class ClientMsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    PROVIDER_LIST("provider_request"),
    ;

    companion object {
        fun fromString(decodeString: String): ClientMsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.Serializable

@Serializable(MsgTypeSerializer::class)
enum class MsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    AUTH_REQUEST("authenticate"),
    AUTH_SUCCESS("auth_success");

    companion object {
        fun fromString(decodeString: String): MsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.Serializable

@Serializable(MsgTypeSerializer::class)
enum class MsgType(
    val str: String,
) {
    GENERIC_ERROR("generic_error"),
    AUTHENTICATE("authenticate");

    companion object {
        fun fromString(decodeString: String): MsgType {
            return entries.single { it.str == decodeString }
        }
    }
}

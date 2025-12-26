package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.Serializable

@Serializable
data class KtcpUnknownMsg(
    val type: MsgType,
)

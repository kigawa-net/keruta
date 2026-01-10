package net.kigawa.keruta.ktcp.model.msg

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class KtcpUnknownMsg(
    val type: MsgType,
)

package net.kigawa.keruta.ktcp.server

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer

interface KtcpConnection {
    suspend fun send(msg: String)
}

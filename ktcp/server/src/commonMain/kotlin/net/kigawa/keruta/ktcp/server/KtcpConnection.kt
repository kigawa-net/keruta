package net.kigawa.keruta.ktcp.server

import kotlinx.serialization.Serializable

interface KtcpConnection {
    suspend fun send(serializer: MsgSerializer, msg: @Serializable Any)
}

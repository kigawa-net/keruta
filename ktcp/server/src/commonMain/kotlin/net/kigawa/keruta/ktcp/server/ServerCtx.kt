package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer

class ServerCtx(
    val session: KtcpSession,
    val serializer: MsgSerializer,
) {
    val connection by session::connection
}

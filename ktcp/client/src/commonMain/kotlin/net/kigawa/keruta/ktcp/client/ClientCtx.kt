package net.kigawa.keruta.ktcp.client

import KtcpSession
import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer
import kotlin.getValue

class ClientCtx(
    val serializer: MsgSerializer,
    val session: KtcpSession
) {
    val connection by session::connection
}

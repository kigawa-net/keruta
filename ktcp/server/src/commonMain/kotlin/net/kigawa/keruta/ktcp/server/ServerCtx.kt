package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.server.session.KtcpSession

class ServerCtx(
    val session: KtcpSession,
    val serializer: KerutaSerializer,
    val server: KtcpServer,
) {
    val connection by session::connection
    val persisterSession by session::persisterSession
}

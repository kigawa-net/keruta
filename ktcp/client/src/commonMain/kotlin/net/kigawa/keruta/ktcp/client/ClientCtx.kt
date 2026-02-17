package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer

class ClientCtx(
    val serializer: KerutaSerializer,
    val session: KtcpSession
) {
    val connection by session::connection
}

package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.domain.serialize.KerutaSerializer

class ClientCtx(
    val serializer: KerutaSerializer,
    val session: KtcpSession
) {
    val connection by session::connection
}

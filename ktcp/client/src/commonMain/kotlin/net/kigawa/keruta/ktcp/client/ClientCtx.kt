package net.kigawa.keruta.ktcp.client

import net.kigawa.keruta.ktcp.client.KtcpSession
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import kotlin.getValue

class ClientCtx(
    val serializer: KerutaSerializer,
    val session: KtcpSession
) {
    val connection by session::connection
}

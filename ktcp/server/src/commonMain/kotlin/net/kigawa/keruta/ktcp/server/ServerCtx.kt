package net.kigawa.keruta.ktcp.server

class ServerCtx(
    val session: KtcpSession,
    val serializer: MsgSerializer,
) {
    val connection by session::connection
}

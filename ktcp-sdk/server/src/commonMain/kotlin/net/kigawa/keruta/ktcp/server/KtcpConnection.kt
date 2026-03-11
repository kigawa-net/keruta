package net.kigawa.keruta.ktcp.server

interface KtcpConnection {
    val server: KtcpServer

    suspend fun send(msg: String)
}

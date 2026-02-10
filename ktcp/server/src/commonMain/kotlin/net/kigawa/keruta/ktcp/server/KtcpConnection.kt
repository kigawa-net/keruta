package net.kigawa.keruta.ktcp.server

interface KtcpConnection {
    suspend fun send(msg: String)
}

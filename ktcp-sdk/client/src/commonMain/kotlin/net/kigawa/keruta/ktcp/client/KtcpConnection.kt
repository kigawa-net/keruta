package net.kigawa.keruta.ktcp.client

interface KtcpConnection {
    suspend fun send(msg: String)
}

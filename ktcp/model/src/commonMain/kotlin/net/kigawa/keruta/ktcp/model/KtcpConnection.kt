package net.kigawa.keruta.ktcp.model

interface KtcpConnection {
    suspend fun send(msg: String)
}

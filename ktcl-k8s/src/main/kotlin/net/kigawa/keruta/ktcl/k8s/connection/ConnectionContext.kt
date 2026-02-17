package net.kigawa.keruta.ktcl.k8s.connection

import net.kigawa.keruta.ktcp.client.ClientCtx

data class ConnectionContext(
    val connection: JvmWebSocketConnection,
    val ctx: ClientCtx,
)

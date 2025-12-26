package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthenticateEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthenticateEntrypoint()
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint()
    )
}

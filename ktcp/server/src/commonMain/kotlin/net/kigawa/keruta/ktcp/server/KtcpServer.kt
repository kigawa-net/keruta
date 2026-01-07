package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.authenticate.ReceiveAuthRequestEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint()
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint()
    )
}

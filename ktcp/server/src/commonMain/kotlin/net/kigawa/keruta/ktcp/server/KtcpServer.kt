package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.auth.AuthSuccessSendEntrypoint
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint()
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint(),
        AuthSuccessSendEntrypoint()
    )
}

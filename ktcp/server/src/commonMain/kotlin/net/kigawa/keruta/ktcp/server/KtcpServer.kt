package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.server.auth.success.AuthSuccessSendEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskCreateEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint(),
        ReceiveTaskCreateEntrypoint()
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint(),
        AuthSuccessSendEntrypoint()
    )
}

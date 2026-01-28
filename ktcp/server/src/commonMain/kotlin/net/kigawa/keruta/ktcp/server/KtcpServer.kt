package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.server.auth.success.SendAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.server.provider.ReceiveProviderListEntrypoint
import net.kigawa.keruta.ktcp.server.provider.SendProviderListedEntrypoint
import net.kigawa.keruta.ktcp.server.queue.*
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskCreateEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint(),
        ReceiveTaskCreateEntrypoint(),
        ReceiveProviderListEntrypoint(),
        ReceiveQueueCreateEntrypoint(),
        ReceiveQueueListEntrypoint(),
        ReceiveQueueShowEntrypoint()
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint(),
        SendAuthSuccessEntrypoint(),
        SendProviderListedEntrypoint(),
        SendQueueCreatedEntrypoint(),
        SendQueueListedEntrypoint(),
        SendQueueShowedEntrypoint(),
    )
}

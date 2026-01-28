package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.server.auth.success.SendAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.server.provider.ReceiveProviderListEntrypoint
import net.kigawa.keruta.ktcp.server.provider.SendProviderListedEntrypoint
import net.kigawa.keruta.ktcp.server.queue.*
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskListEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskShowEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskCreatedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskListedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskShowedEntrypoint

class KtcpServer {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint(),
        ReceiveTaskCreateEntrypoint(),
        ReceiveProviderListEntrypoint(),
        ReceiveQueueCreateEntrypoint(),
        ReceiveQueueListEntrypoint(),
        ReceiveQueueShowEntrypoint(),
        ReceiveTaskListEntrypoint(),
        ReceiveTaskShowEntrypoint(),
    )
    val clientEntrypoints = KtcpClientEntrypoints(
        SendGenericErrEntrypoint(),
        SendAuthSuccessEntrypoint(),
        SendProviderListedEntrypoint(),
        SendQueueCreatedEntrypoint(),
        SendQueueListedEntrypoint(),
        SendQueueShowedEntrypoint(),
        SendTaskCreatedEntrypoint(),
        SendTaskListedEntrypoint(),
        SendTaskShowedEntrypoint(),
    )
}

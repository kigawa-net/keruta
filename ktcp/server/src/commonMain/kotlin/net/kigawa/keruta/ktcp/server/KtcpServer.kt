package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.server.auth.success.SendAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddEntrypoint
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.server.provider.ReceiveProviderListEntrypoint
import net.kigawa.keruta.ktcp.server.provider.SendProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcp.server.provider.SendProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcp.server.provider.SendProviderListedEntrypoint
import net.kigawa.keruta.ktcp.server.queue.*
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskListEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskMoveEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskShowEntrypoint
import net.kigawa.keruta.ktcp.server.task.ReceiveTaskUpdateEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskCreatedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskListedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskMovedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskShowedEntrypoint
import net.kigawa.keruta.ktcp.server.task.SendTaskUpdatedEntrypoint

class KtcpServer(
    providerAddEntrypoint: ServerProviderAddEntrypoint<ServerCtx>,
    providerCompleteEntrypoint: ServerProviderCompleteEntrypoint<ServerCtx>,
) {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint(),
        ReceiveTaskCreateEntrypoint(),
        ReceiveTaskUpdateEntrypoint(),
        ReceiveTaskMoveEntrypoint(),
        ReceiveProviderListEntrypoint(),
        providerAddEntrypoint,
        providerCompleteEntrypoint,
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
        SendProviderAddTokenEntrypoint(),
        SendProviderIdpAddedEntrypoint(),
        SendQueueCreatedEntrypoint(),
        SendQueueListedEntrypoint(),
        SendQueueShowedEntrypoint(),
        SendTaskCreatedEntrypoint(),
        SendTaskUpdatedEntrypoint(),
        SendTaskMovedEntrypoint(),
        SendTaskListedEntrypoint(),
        SendTaskShowedEntrypoint(),
    )
}

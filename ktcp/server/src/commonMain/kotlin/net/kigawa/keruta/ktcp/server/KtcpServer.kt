package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.KtcpServerEntrypoints
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifier
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderRegisterTokenEntrypoint
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.model.provider.delete.ServerProviderDeleteEntrypoint
import net.kigawa.keruta.ktcp.server.auth.ReceiveAuthRequestEntrypoint
import net.kigawa.keruta.ktcp.server.auth.success.SendAuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.server.provider.*
import net.kigawa.keruta.ktcp.server.queue.*
import net.kigawa.keruta.ktcp.server.task.*

class KtcpServer(
    providerRegisterTokenEntrypoint: ServerProviderRegisterTokenEntrypoint<ServerCtx>,
    providerCompleteEntrypoint: ServerProviderCompleteEntrypoint<ServerCtx>,
    providerDeleteEntrypoint: ServerProviderDeleteEntrypoint<ServerCtx>,
    val jwtVerifier: JwtVerifier,
) {

    val ktcpServerEntrypoints = KtcpServerEntrypoints(
        ReceiveAuthRequestEntrypoint(),
        ReceiveTaskCreateEntrypoint(),
        ReceiveTaskUpdateEntrypoint(),
        ReceiveTaskMoveEntrypoint(),
        ReceiveProviderListEntrypoint(),
        providerRegisterTokenEntrypoint,
        providerCompleteEntrypoint,
        providerDeleteEntrypoint,
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
        SendProviderDeletedEntrypoint(),
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

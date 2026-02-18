package net.kigawa.keruta.ktcl.k8s.entrypoint

import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.task.TaskExecutor
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints

class ClientEntrypointsFactory(
    private val ktcpClient: KtcpClient,
    private val config: K8sConfig,
    private val taskExecutor: TaskExecutor,
) {
    fun create(): KtcpClientEntrypoints<ClientCtx> {
        return KtcpClientEntrypoints(
            genericErrEntrypoint = ReceiveGenericErrEntrypoint(),
            authSuccessEntrypoint = ReceiveAuthSuccessEntrypoint(),
            providerListEntrypoint = ReceiveProviderListedEntrypoint(),
            providerAddTokenEntrypoint = ReceiveProviderAddTokenEntrypoint(),
            providerIdpAddedEntrypoint = ReceiveProviderIdpAddedEntrypoint(),
            providerDeletedEntrypoint = ReceiveProviderDeletedEntrypoint(),
            queueCreatedEntrypoint = ReceiveQueueCreatedEntrypoint(),
            queueListedEntrypoint = ReceiveQueueListedEntrypoint(),
            queueShowedEntrypoint = ReceiveQueueShowedEntrypoint(),
            taskCreatedEntrypoint = ReceiveTaskCreatedEntrypoint(ktcpClient, config.queueId),
            taskUpdatedEntrypoint = ReceiveTaskUpdatedEntrypoint(),
            taskMovedEntrypoint = ReceiveTaskMovedEntrypoint(),
            taskListedEntrypoint = ReceiveTaskListedEntrypoint(taskExecutor),
            taskShowedEntrypoint = ReceiveTaskShowedEntrypoint(taskExecutor),
        )
    }
}

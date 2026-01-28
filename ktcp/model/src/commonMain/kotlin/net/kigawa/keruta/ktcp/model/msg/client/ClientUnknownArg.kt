package net.kigawa.keruta.ktcp.model.msg.client

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedArg
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.model.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.model.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.model.task.showed.ClientTaskShowedMsg
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedMsg
import net.kigawa.kodel.api.err.Res

interface ClientUnknownArg {
    fun tryToGenericError(): Res<ClientGenericErrArg, KtcpErr>?
    fun tryToAuthSuccess(): Res<ClientAuthSuccessArg, KtcpErr>?
    fun tryToProviderList(): Res<ClientProviderListedArg, KtcpErr>?
    fun tryToQueueCreated(): Res<ClientQueueCreatedMsg, KtcpErr>?
    fun tryToQueueListed(): Res<ClientQueueListedMsg, KtcpErr>?
    fun tryToQueueShowed(): Res<ClientQueueShowedMsg, KtcpErr>?
    fun tryToTaskCreated(): Res<ClientTaskCreatedMsg, KtcpErr>?
    fun tryToTaskShowed(): Res<ClientTaskShowedMsg, KtcpErr>?
    fun tryToTaskListed(): Res<ClientTaskListedMsg, KtcpErr>?
    fun tryToTaskUpdated(): Res<ClientTaskUpdatedMsg, KtcpErr>?

}

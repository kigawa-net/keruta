package net.kigawa.keruta.ktcp.domain.msg.client

import net.kigawa.keruta.ktcp.domain.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.domain.err.GenericErrMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.keruta.ktcp.domain.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.domain.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedMsg
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedMsg
import net.kigawa.keruta.ktcp.domain.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.domain.task.moved.ClientTaskMovedMsg
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedMsg
import net.kigawa.keruta.ktcp.domain.task.updated.ClientTaskUpdatedMsg
import net.kigawa.kodel.api.err.Res

interface ClientUnknownArg {
    fun tryToGenericError(): Res<GenericErrMsg, KtcpErr>?
    fun tryToAuthSuccess(): Res<ClientAuthSuccessMsg, KtcpErr>?
    fun tryToProviderList(): Res<ClientProviderListedMsg, KtcpErr>?
    fun tryToProviderAddToken(): Res<ClientProviderAddTokenMsg, KtcpErr>?
    fun tryToProviderIdpAdded(): Res<ClientProviderIdpAddedMsg, KtcpErr>?
    fun tryToProviderDeleted(): Res<ClientProviderDeletedMsg, KtcpErr>?
    fun tryToQueueCreated(): Res<ClientQueueCreatedMsg, KtcpErr>?
    fun tryToQueueListed(): Res<ClientQueueListedMsg, KtcpErr>?
    fun tryToQueueShowed(): Res<ClientQueueShowedMsg, KtcpErr>?
    fun tryToTaskCreated(): Res<ClientTaskCreatedMsg, KtcpErr>?
    fun tryToTaskShowed(): Res<ClientTaskShowedMsg, KtcpErr>?
    fun tryToTaskListed(): Res<ClientTaskListedMsg, KtcpErr>?
    fun tryToTaskUpdated(): Res<ClientTaskUpdatedMsg, KtcpErr>?
    fun tryToTaskMoved(): Res<ClientTaskMovedMsg, KtcpErr>?
    fun tryToQueueUpdated(): Res<ClientQueueUpdatedMsg, KtcpErr>?
    fun tryToQueueDeleted(): Res<ClientQueueDeletedMsg, KtcpErr>?

}

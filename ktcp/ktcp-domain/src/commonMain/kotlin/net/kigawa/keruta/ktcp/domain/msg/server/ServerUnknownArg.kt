package net.kigawa.keruta.ktcp.domain.msg.server

import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.err.GenericErrMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenMsg
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteMsg
import net.kigawa.keruta.ktcp.domain.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.domain.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateMsg
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.domain.task.move.ServerTaskMoveMsg
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.domain.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.err.Res

interface ServerUnknownArg {
    fun tryToGenericError(): Res<GenericErrMsg, KtcpErr>?
    fun tryToAuthenticate(): Res<ServerAuthRequestMsg, KtcpErr>?
    fun tryToTaskCreate(): Res<ServerTaskCreateMsg, KtcpErr>?
    fun tryToProvidersRequest(): Res<ServerProviderListMsg, KtcpErr>?
    fun tryToProviderIssueToken(): Res<ServerProviderIssueTokenMsg, KtcpErr>?
    fun tryToProviderComplete(): Res<ServerProviderCompleteMsg, KtcpErr>?
    fun tryToProviderDelete(): Res<ServerProviderDeleteMsg, KtcpErr>?
    fun tryToQueueCreate(): Res<ServerQueueCreateMsg, KtcpErr>?
    fun tryToQueueList(): Res<ServerQueueListMsg, KtcpErr>?
    fun tryToQueueShow(): Res<ServerQueueShowMsg, KtcpErr>?
    fun tryToTaskList(): Res<ServerTaskListMsg, KtcpErr>?
    fun tryToTaskShow(): Res<ServerTaskShowMsg, KtcpErr>?
    fun tryToTaskUpdate(): Res<ServerTaskUpdateMsg, KtcpErr>?
    fun tryToTaskMove(): Res<ServerTaskMoveMsg, KtcpErr>?
    fun tryToQueueUpdate(): Res<ServerQueueUpdateMsg, KtcpErr>?
}

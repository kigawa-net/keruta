package net.kigawa.keruta.ktcp.model.msg.server

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddMsg
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.model.provider.delete.ServerProviderDeleteMsg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.move.ServerTaskMoveMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.err.Res

interface ServerUnknownArg {
    fun tryToGenericError(): Res<GenericErrMsg, KtcpErr>?
    fun tryToAuthenticate(): Res<ServerAuthRequestMsg, KtcpErr>?
    fun tryToTaskCreate(): Res<ServerTaskCreateMsg, KtcpErr>?
    fun tryToProvidersRequest(): Res<ServerProviderListMsg, KtcpErr>?
    fun tryToProviderAdd(): Res<ServerProviderAddMsg, KtcpErr>?
    fun tryToProviderComplete(): Res<ServerProviderCompleteMsg, KtcpErr>?
    fun tryToProviderDelete(): Res<ServerProviderDeleteMsg, KtcpErr>?
    fun tryToQueueCreate(): Res<ServerQueueCreateMsg, KtcpErr>?
    fun tryToQueueList(): Res<ServerQueueListMsg, KtcpErr>?
    fun tryToQueueShow(): Res<ServerQueueShowMsg, KtcpErr>?
    fun tryToTaskList(): Res<ServerTaskListMsg, KtcpErr>?
    fun tryToTaskShow(): Res<ServerTaskShowMsg, KtcpErr>?
    fun tryToTaskUpdate(): Res<ServerTaskUpdateMsg, KtcpErr>?
    fun tryToTaskMove(): Res<ServerTaskMoveMsg, KtcpErr>?
}
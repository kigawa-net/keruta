package net.kigawa.keruta.ktcp.model.msg.server

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.err.Res

interface ServerUnknownArg {
    fun tryToGenericError(): Res<ClientGenericErrArg, KtcpErr>?
    fun tryToAuthenticate(): Res<ServerAuthRequestArg, KtcpErr>?
    fun tryToTaskCreate(): Res<ServerTaskCreateMsg, KtcpErr>?
    fun tryToProvidersRequest(): Res<ServerProviderListArg, KtcpErr>?
    fun tryToQueueCreate(): Res<ServerQueueCreateMsg, KtcpErr>?
    fun tryToQueueList(): Res<ServerQueueListMsg, KtcpErr>?
    fun tryToQueueShow(): Res<ServerQueueShowMsg, KtcpErr>?
    fun tryToTaskList(): Res<ServerTaskListMsg, KtcpErr>?
    fun tryToTaskShow(): Res<ServerTaskShowMsg, KtcpErr>?
    fun tryToTaskUpdate(): Res<ServerTaskUpdateMsg, KtcpErr>?
}

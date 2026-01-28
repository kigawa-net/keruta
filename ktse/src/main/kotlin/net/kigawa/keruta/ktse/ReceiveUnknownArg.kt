package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownArg
import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownMsg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.move.ServerTaskMoveMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktse.auth.ReceiveAuthRequestArg
import net.kigawa.keruta.ktse.err.ReceiveGenericErrArg
import net.kigawa.keruta.ktse.provider.ReceiveProviderListArg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveUnknownArg(
    val msg: ServerUnknownMsg,
    val ctx: ServerCtx,
    val text: String,
): ServerUnknownArg {
    override fun tryToGenericError(): Res<ReceiveGenericErrArg, DecodeFrameErr>? {
        if (msg.type != ServerMsgType.GENERIC_ERROR) return null
        return translate<GenericErrMsg, ReceiveGenericErrArg> { ReceiveGenericErrArg(it) }
    }

    override fun tryToAuthenticate(): Res<ReceiveAuthRequestArg, DecodeFrameErr>? {
        if (msg.type != ServerMsgType.AUTH_REQUEST) return null
        return translate<ServerAuthRequestMsg, ReceiveAuthRequestArg> { ReceiveAuthRequestArg(it) }
    }

    override fun tryToTaskCreate(): Res<ServerTaskCreateMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_CREATE) return null
        return translate()
    }

    override fun tryToProvidersRequest(): Res<ServerProviderListArg, KtcpErr>? {
        if (msg.type != ServerMsgType.PROVIDER_LIST) return null
        return translate<ServerProviderListMsg, ServerProviderListArg> {
            ReceiveProviderListArg(it)
        }
    }

    override fun tryToQueueCreate(): Res<ServerQueueCreateMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.QUEUE_CREATE) return null
        return translate()
    }

    override fun tryToQueueList(): Res<ServerQueueListMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.QUEUE_LIST) return null
        return translate()
    }

    override fun tryToQueueShow(): Res<ServerQueueShowMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.QUEUE_SHOW) return null
        return translate()
    }

    override fun tryToTaskList(): Res<ServerTaskListMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_LIST) return null
        return translate()
    }

    override fun tryToTaskShow(): Res<ServerTaskShowMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_SHOW) return null
        return translate()
    }

    override fun tryToTaskUpdate(): Res<ServerTaskUpdateMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_UPDATE) return null
        return translate()
    }

    override fun tryToTaskMove(): Res<ServerTaskMoveMsg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_MOVE) return null
        return translate()
    }

    inline fun <reified T> translate(): Res<T, DecodeFrameErr> {
        return translate<T, T> { it }
    }

    inline fun <reified T, R> translate(initialize: ReceiveUnknownArg.(T) -> R): Res<R, DecodeFrameErr> {
        return when (
            val msg = ctx.serializer.deserialize<T>(text)
        ) {
            is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
            is Res.Ok<T, *> -> Res.Ok(
                initialize(msg.value)
            )
        }
    }

    companion object {
        val logger = getKogger()
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            val text = frame.readText()
            logger.debug("frameText: $text")
            return when (
                val msg = ctx.serializer.deserialize<ServerUnknownMsg>(text)
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
                is Res.Ok<ServerUnknownMsg, *> -> Res.Ok(
                    ReceiveUnknownArg(
                        msg.value, ctx, text
                    )
                )
            }
        }
    }
}

package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType
import net.kigawa.keruta.ktcp.model.msg.ServerUnknownArg
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateArg
import net.kigawa.keruta.ktcp.model.task.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktse.auth.ReceiveAuthRequestArg
import net.kigawa.keruta.ktse.err.ReceiveGenericErrArg
import net.kigawa.keruta.ktse.task.ReceiveTaskCreateArg
import net.kigawa.kodel.api.err.Res

class ReceiveUnknownArg(
    val msg: KtcpUnknownMsg,
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

    override fun tryToTaskCreate(): Res<ServerTaskCreateArg, KtcpErr>? {
        if (msg.type != ServerMsgType.TASK_CREATE) return null
        return translate<ServerTaskCreateMsg, ReceiveTaskCreateArg> { ReceiveTaskCreateArg(it) }
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
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            val text = frame.readText()
            return when (
                val msg = ctx.serializer.deserialize<KtcpUnknownMsg>(text)
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
                is Res.Ok<KtcpUnknownMsg, *> -> Res.Ok(
                    ReceiveUnknownArg(
                        msg.value, ctx, text
                    )
                )
            }
        }
    }
}

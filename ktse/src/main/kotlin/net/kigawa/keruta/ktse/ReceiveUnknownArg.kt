package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.msg.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.msg.ServerMsgType
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktse.auth.AuthSuccessSendArg
import net.kigawa.keruta.ktse.auth.ReceiveAuthRequestArg
import net.kigawa.keruta.ktse.err.ReceiveGenericErrArg
import net.kigawa.kodel.api.err.Res

class ReceiveUnknownArg(
    val msg: KtcpUnknownMsg,
    val frame: Frame.Text,
    val ctx: ServerCtx,
): UnknownArg {
    override fun tryToGenericError(): Res<ReceiveGenericErrArg, DecodeFrameErr>? {
        if (msg.type != ServerMsgType.GENERIC_ERROR) return null
        return ReceiveGenericErrArg.fromFrame(frame, ctx)
    }

    override fun tryToAuthenticate(): Res<ReceiveAuthRequestArg, DecodeFrameErr>? {
        if (msg.type != ServerMsgType.AUTH_REQUEST) return null
        return ReceiveAuthRequestArg.fromFrame(frame, ctx)
    }

    override fun tryToAuthSuccess(): Res<ClientAuthSuccessArg, DecodeFrameErr>? {
        if (msg.type != ServerMsgType.AUTH_SUCCESS) return null
        return AuthSuccessSendArg.fromFrame(frame, ctx)
    }

    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            return when (
                val msg = ctx.serializer.deserialize<KtcpUnknownMsg>(frame.readText())
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
                is Res.Ok<KtcpUnknownMsg, *> -> Res.Ok(
                    ReceiveUnknownArg(
                        msg.value, frame, ctx
                    )
                )
            }
        }
    }
}

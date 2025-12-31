package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.msg.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.keruta.ktcp.model.serialize.DeserializeErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.model.serialize.DecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.InvalidTypeDecodeFrameErr
import net.kigawa.kodel.api.err.Res

class ReceiveUnknownArg(
    val msg: KtcpUnknownMsg,
    val frame: Frame.Text,
    val ctx: ServerCtx,
): UnknownArg {
    override fun tryToGenericError(): GenericErrArg? {
        if (msg.type != MsgType.GENERIC_ERROR) return null
        return ReceiveGenericErrArg()
    }

    override fun tryToAuthenticate(): Res<ReceiveAuthenticateArg, DecodeFrameErr>? {
        if (msg.type != MsgType.AUTHENTICATE) return null
        return ReceiveAuthenticateArg.fromFrame(frame, ctx)
    }

    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<KtcpUnknownMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<KtcpUnknownMsg, *> -> Res.Ok(
                    ReceiveUnknownArg(
                        msg.value, frame, ctx
                    )
                )
            }
        }
    }
}

package net.kigawa.keruta.ktse.auth

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class ReceiveAuthRequestArg(
    override val authRequestMsg: ServerAuthRequestMsg,
): AuthRequestArg {

    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveAuthRequestArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            return when (
                val msg = ctx.serializer.deserialize<ServerAuthRequestMsg>(frame.readText())
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("",it) }
                is Res.Ok<ServerAuthRequestMsg, *> -> Res.Ok(
                    ReceiveAuthRequestArg(
                        msg.value
                    )
                )
            }
        }
    }
}

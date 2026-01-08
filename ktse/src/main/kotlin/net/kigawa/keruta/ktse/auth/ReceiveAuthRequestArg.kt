package net.kigawa.keruta.ktse.auth

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.types.DecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeErr
import net.kigawa.keruta.ktcp.model.err.types.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.*
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class ReceiveAuthRequestArg(
    override val authRequestMsg: AuthRequestMsg,
): AuthRequestArg {


    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveAuthRequestArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<AuthRequestMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<AuthRequestMsg, *> -> Res.Ok(
                    ReceiveAuthRequestArg(
                        msg.value
                    )
                )
            }
        }
    }
}

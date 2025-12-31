package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateArg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.serialize.DeserializeErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.model.serialize.DecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.InvalidTypeDecodeFrameErr
import net.kigawa.kodel.api.err.Res

class ReceiveAuthenticateArg(value: AuthenticateMsg, frame: Frame.Text, ctx: ServerCtx): AuthenticateArg {
    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveAuthenticateArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<AuthenticateMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<AuthenticateMsg, *> -> Res.Ok(
                    ReceiveAuthenticateArg(
                        msg.value, frame, ctx
                    )
                )
            }
        }
    }
}

package net.kigawa.keruta.ktse.auth

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.AuthenticateArg
import net.kigawa.keruta.ktcp.model.auth.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.err.types.DecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeErr
import net.kigawa.keruta.ktcp.model.err.types.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.*
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class ReceiveAuthenticateArg(
    override val authenticateMsg: AuthenticateMsg,
): AuthenticateArg {


    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveAuthenticateArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<AuthenticateMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<AuthenticateMsg, *> -> Res.Ok(
                    ReceiveAuthenticateArg(
                        msg.value
                    )
                )
            }
        }
    }
}

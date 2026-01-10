package net.kigawa.keruta.ktse.auth

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessMsg
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.kodel.api.err.Res

class AuthSuccessSendArg(
    override val authSuccessMsg: AuthSuccessMsg,
): ClientAuthSuccessArg {

    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<AuthSuccessSendArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("",null))
            return when (
                val msg = ctx.serializer.deserialize<AuthSuccessMsg>(frame.readText())
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("",it) }
                is Res.Ok<AuthSuccessMsg, *> -> Res.Ok(
                    AuthSuccessSendArg(
                        msg.value
                    )
                )
            }
        }
    }
}

package net.kigawa.keruta.ktse.auth

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessArg
import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessMsg
import net.kigawa.keruta.ktcp.model.err.types.DecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.err.types.DeserializeErr
import net.kigawa.keruta.ktcp.model.err.types.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class AuthSuccessSendArg(
    override val authSuccessMsg: AuthSuccessMsg,
): AuthSuccessArg {

    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<AuthSuccessSendArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<AuthSuccessMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<AuthSuccessMsg, *> -> Res.Ok(
                    AuthSuccessSendArg(
                        msg.value
                    )
                )
            }
        }
    }
}

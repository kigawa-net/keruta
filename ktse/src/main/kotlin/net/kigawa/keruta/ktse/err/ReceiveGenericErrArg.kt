package net.kigawa.keruta.ktse.err

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class ReceiveGenericErrArg(
    override val msg: GenericErrMsg,
): GenericErrArg {
    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveGenericErrArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            return when (
                val msg = ctx.serializer.deserialize<GenericErrMsg>(frame.readText())
            ) {
                is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
                is Res.Ok<GenericErrMsg, *> -> Res.Ok(
                    ReceiveGenericErrArg(
                        msg.value
                    )
                )
            }
        }
    }
}

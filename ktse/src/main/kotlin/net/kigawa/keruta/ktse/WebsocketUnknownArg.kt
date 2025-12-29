package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.msg.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.serialize.DeserializeErr
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktse.reader.DecodeFrameErr
import net.kigawa.keruta.ktse.reader.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktse.reader.InvalidTypeDecodeFrameErr
import net.kigawa.kodel.api.err.Res

class WebsocketUnknownArg(
    val msg: KtcpUnknownMsg,
) {
    companion object {
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<WebsocketUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr())
            return when (
                val msg = ctx.serializer.deserialize<KtcpUnknownMsg>(frame.readText())
            ) {
                is Res.Err<*, DeserializeErr> -> msg.mapErr { DeserializeDecodeFrameErr(it) }
                is Res.Ok<KtcpUnknownMsg, *> -> Res.Ok(WebsocketUnknownArg(msg.value))
            }
        }
    }
}

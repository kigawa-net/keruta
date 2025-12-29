package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktse.reader.FrameDecodeErr
import net.kigawa.keruta.ktse.reader.InvalidFormatFrameErr
import net.kigawa.keruta.ktse.reader.InvalidFrameTypeErr
import net.kigawa.kodel.api.err.Res
import tools.jackson.core.JacksonException
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule

object FrameReader {
    val mapper = jsonMapper {
        addModules(kotlinModule())

    }

    fun readToMsg(frame: Frame): Res<WebsocketUnknownMsg, FrameDecodeErr> {
        if (frame !is Frame.Text) return Res.Err(InvalidFrameTypeErr())
        val node = try {
            mapper.readTree(frame.readText())
        } catch (e: JacksonException) {
            return Res.Err(InvalidFormatFrameErr(e))
        }

        return Res.Ok(
            WebsocketUnknownMsg(
                frame, node
            )
        )
    }

}

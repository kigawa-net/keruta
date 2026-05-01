package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.err.GenericErrMsg
import net.kigawa.keruta.ktcp.domain.err.IllegalFormatDeserializeErr
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.domain.msg.server.ServerUnknownArg
import net.kigawa.keruta.ktcp.domain.provider.add.ServerProviderIssueTokenMsg
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.domain.provider.delete.ServerProviderDeleteMsg
import net.kigawa.keruta.ktcp.domain.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.domain.queue.delete.ServerQueueDeleteMsg
import net.kigawa.keruta.ktcp.domain.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateMsg
import net.kigawa.keruta.ktcp.domain.serialize.deserialize
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.domain.task.move.ServerTaskMoveMsg
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.domain.task.update.ServerTaskUpdateMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.DecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.DeserializeDecodeFrameErr
import net.kigawa.keruta.ktcp.server.err.InvalidTypeDecodeFrameErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class ReceiveUnknownArg(
    val typeStr: String,
    val ctx: ServerCtx,
    val text: String,
): ServerUnknownArg {
    override fun tryToGenericError(): Res<GenericErrMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.GENERIC_ERROR.str) return null
        return translate()
    }

    override fun tryToAuthenticate(): Res<ServerAuthRequestMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.AUTH_REQUEST.str) return null
        return translate()
    }

    override fun tryToTaskCreate(): Res<ServerTaskCreateMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.TASK_CREATE.str) return null
        return translate()
    }

    override fun tryToProvidersRequest(): Res<ServerProviderListMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.PROVIDER_LIST.str) return null
        return translate()
    }

    override fun tryToProviderIssueToken(): Res<ServerProviderIssueTokenMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.PROVIDER_ISSUE_TOKEN.str) return null
        return translate()
    }

    override fun tryToProviderComplete(): Res<ServerProviderCompleteMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.PROVIDER_COMPLETE.str) return null
        return translate()
    }

    override fun tryToProviderDelete(): Res<ServerProviderDeleteMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.PROVIDER_DELETE.str) return null
        return translate()
    }

    override fun tryToQueueCreate(): Res<ServerQueueCreateMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.QUEUE_CREATE.str) return null
        return translate()
    }

    override fun tryToQueueList(): Res<ServerQueueListMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.QUEUE_LIST.str) return null
        return translate()
    }

    override fun tryToQueueShow(): Res<ServerQueueShowMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.QUEUE_SHOW.str) return null
        return translate()
    }

    override fun tryToTaskList(): Res<ServerTaskListMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.TASK_LIST.str) return null
        return translate()
    }

    override fun tryToTaskShow(): Res<ServerTaskShowMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.TASK_SHOW.str) return null
        return translate()
    }

    override fun tryToTaskUpdate(): Res<ServerTaskUpdateMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.TASK_UPDATE.str) return null
        return translate()
    }

    override fun tryToTaskMove(): Res<ServerTaskMoveMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.TASK_MOVE.str) return null
        return translate()
    }

    override fun tryToQueueUpdate(): Res<ServerQueueUpdateMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.QUEUE_UPDATE.str) return null
        return translate()
    }

    override fun tryToQueueDelete(): Res<ServerQueueDeleteMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.QUEUE_DELETE.str) return null
        return translate()
    }

    inline fun <reified T> translate(): Res<T, DecodeFrameErr> {
        return translate<T, T> { it }
    }

    inline fun <reified T, R> translate(initialize: ReceiveUnknownArg.(T) -> R): Res<R, DecodeFrameErr> {
        return when (
            val msg = ctx.serializer.deserialize<T>(text)
        ) {
            is Res.Err<*, KtcpErr> -> msg.mapErr { DeserializeDecodeFrameErr("", it) }
            is Res.Ok<T, *> -> Res.Ok(
                initialize(msg.value)
            )
        }
    }

    companion object {
        val logger = getKogger()
        fun fromFrame(frame: Frame, ctx: ServerCtx): Res<ReceiveUnknownArg, DecodeFrameErr> {
            if (frame !is Frame.Text) return Res.Err(InvalidTypeDecodeFrameErr("", null))
            val text = frame.readText()
            logger.debug("frameText: $text")

            // First, extract type field from JSON without full deserialization
            val jsonElement = Json.parseToJsonElement(text)
            val typeStr = (jsonElement as? JsonObject)?.get("type")?.jsonPrimitive?.content ?: return Res.Err(
                DeserializeDecodeFrameErr(
                    "",
                    IllegalFormatDeserializeErr(
                        "type field is required", SerializationException("type field is required")
                    )
                )
            )

            return Res.Ok(ReceiveUnknownArg(typeStr, ctx, text))
        }
    }
}

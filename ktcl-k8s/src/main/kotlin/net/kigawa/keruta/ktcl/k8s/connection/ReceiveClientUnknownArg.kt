package net.kigawa.keruta.ktcl.k8s.connection

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcp.domain.auth.sccess.ClientAuthSuccessMsg
import net.kigawa.keruta.ktcp.domain.err.GenericErrMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.domain.msg.client.ClientUnknownArg
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.domain.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.domain.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.keruta.ktcp.domain.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.domain.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedMsg
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedMsg
import net.kigawa.keruta.ktcp.domain.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.domain.serialize.deserialize
import net.kigawa.keruta.ktcp.domain.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.domain.task.moved.ClientTaskMovedMsg
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedMsg
import net.kigawa.keruta.ktcp.domain.task.updated.ClientTaskUpdatedMsg
import net.kigawa.kodel.api.err.Res

class ReceiveClientUnknownArg(
    private val typeStr: String,
    private val serializer: KerutaSerializer,
    private val text: String,
): ClientUnknownArg {

    override fun tryToGenericError(): Res<GenericErrMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.GENERIC_ERROR.str) return null
        return serializer.deserialize<GenericErrMsg>(text)
    }

    override fun tryToAuthSuccess(): Res<ClientAuthSuccessMsg, KtcpErr>? {
        if (typeStr != ServerMsgType.AUTH_SUCCESS.str) return null
        return serializer.deserialize<ClientAuthSuccessMsg>(text)
    }

    override fun tryToProviderList(): Res<ClientProviderListedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.PROVIDER_LISTED.str) return null
        return serializer.deserialize<ClientProviderListedMsg>(text)
    }

    override fun tryToProviderAddToken(): Res<ClientProviderAddTokenMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.PROVIDER_ADD_TOKEN_ISSUED.str) return null
        return serializer.deserialize<ClientProviderAddTokenMsg>(text)
    }

    override fun tryToProviderIdpAdded(): Res<ClientProviderIdpAddedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.PROVIDER_IDP_ADDED.str) return null
        return serializer.deserialize<ClientProviderIdpAddedMsg>(text)
    }

    override fun tryToProviderDeleted(): Res<ClientProviderDeletedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.PROVIDER_DELETED.str) return null
        return serializer.deserialize<ClientProviderDeletedMsg>(text)
    }

    override fun tryToQueueCreated(): Res<ClientQueueCreatedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_CREATED.str) return null
        return serializer.deserialize<ClientQueueCreatedMsg>(text)
    }

    override fun tryToQueueListed(): Res<ClientQueueListedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_LISTED.str) return null
        return serializer.deserialize<ClientQueueListedMsg>(text)
    }

    override fun tryToQueueShowed(): Res<ClientQueueShowedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_SHOWED.str) return null
        return serializer.deserialize<ClientQueueShowedMsg>(text)
    }

    override fun tryToQueueUpdated(): Res<ClientQueueUpdatedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_UPDATED.str) return null
        return serializer.deserialize<ClientQueueUpdatedMsg>(text)
    }

    override fun tryToQueueDeleted(): Res<ClientQueueDeletedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_DELETED.str) return null
        return serializer.deserialize<ClientQueueDeletedMsg>(text)
    }

    override fun tryToTaskCreated(): Res<ClientTaskCreatedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.TASK_CREATED.str) return null
        return serializer.deserialize<ClientTaskCreatedMsg>(text)
    }

    override fun tryToTaskShowed(): Res<ClientTaskShowedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.TASK_SHOWED.str) return null
        return serializer.deserialize<ClientTaskShowedMsg>(text)
    }

    override fun tryToTaskListed(): Res<ClientTaskListedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.TASK_LISTED.str) return null
        return serializer.deserialize<ClientTaskListedMsg>(text)
    }

    override fun tryToTaskUpdated(): Res<ClientTaskUpdatedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.TASK_UPDATED.str) return null
        return serializer.deserialize<ClientTaskUpdatedMsg>(text)
    }

    override fun tryToTaskMoved(): Res<ClientTaskMovedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.TASK_MOVED.str) return null
        return serializer.deserialize<ClientTaskMovedMsg>(text)
    }

    override fun tryToQueueUpdated(): Res<ClientQueueUpdatedMsg, KtcpErr>? {
        if (typeStr != ClientMsgType.QUEUE_UPDATED.str) return null
        return serializer.deserialize<ClientQueueUpdatedMsg>(text)
    }

    companion object {
        fun fromText(text: String, serializer: KerutaSerializer): ReceiveClientUnknownArg? {
            val jsonElement = Json.parseToJsonElement(text)
            val typeStr = (jsonElement as? JsonObject)?.get("type")?.jsonPrimitive?.content ?: return null
            return ReceiveClientUnknownArg(typeStr, serializer, text)
        }
    }
}

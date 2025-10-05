package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.client.ApiReader

interface SubscribeApiAction {
    val apiPacketReceiveAction: ApiPacketReceiveAction
    val closed: Boolean
    suspend fun subscribe(reader: ApiReader) {
        while (!closed) {
            val data = reader.read() ?: break
            apiPacketReceiveAction.receive(data)
        }
    }

}

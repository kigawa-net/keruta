package net.kigawa.keruta.sdk.client.model.client

interface ApiSender {
    suspend fun send(data: ApiSendData)
}

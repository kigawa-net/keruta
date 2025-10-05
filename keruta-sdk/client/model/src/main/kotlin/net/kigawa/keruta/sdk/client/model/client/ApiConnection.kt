package net.kigawa.keruta.sdk.client.model.client

interface ApiConnection {
    fun useReader(): ApiReader
    fun getSender(): ApiSender
}

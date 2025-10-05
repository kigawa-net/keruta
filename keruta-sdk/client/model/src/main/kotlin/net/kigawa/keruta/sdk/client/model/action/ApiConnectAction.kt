package net.kigawa.keruta.sdk.client.model.action

import net.kigawa.keruta.sdk.client.model.client.ApiConnection

interface ApiConnectAction {
    suspend fun connect(): ApiConnection
}

package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg
import net.kigawa.kodel.api.net.Url

interface PersistedProvider {
    fun asProviderListProvider(): ClientProviderListedMsg.Provider
    val audience: String
    val issuer: Url
    val id: Long
}

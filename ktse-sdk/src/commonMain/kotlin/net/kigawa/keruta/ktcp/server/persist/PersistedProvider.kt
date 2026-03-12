package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedMsg
import net.kigawa.kodel.api.net.Url

interface PersistedProvider {
    fun asProviderListProvider(): ClientProviderListedMsg.Provider
    val name: String
    val audience: String
    val issuer: Url
    val id: Long
}

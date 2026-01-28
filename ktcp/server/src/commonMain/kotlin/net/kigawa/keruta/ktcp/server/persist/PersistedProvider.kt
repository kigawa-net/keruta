package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.server.auth.Idp

interface PersistedProvider {
    fun asUserIdp(subject: String): Idp
    fun asProviderListProvider(): ClientProviderListedMsg.Provider
}

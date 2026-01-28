package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListedArg
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListedMsg

class SendProviderListedArg(
    override val msg: ClientProviderListedMsg,
): ClientProviderListedArg

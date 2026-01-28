package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedArg
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg

class SendProviderListedArg(
    override val msg: ClientProviderListedMsg,
): ClientProviderListedArg

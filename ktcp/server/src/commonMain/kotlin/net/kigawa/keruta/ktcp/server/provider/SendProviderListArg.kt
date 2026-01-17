package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListMsg

class SendProviderListArg(
    override val msg: ClientProviderListMsg,
): ClientProviderListArg

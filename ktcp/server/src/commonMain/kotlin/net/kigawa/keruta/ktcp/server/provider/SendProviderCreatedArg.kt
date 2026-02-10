package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedArg
import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedMsg

class SendProviderCreatedArg(
    override val msg: ClientProviderCreatedMsg,
): ClientProviderCreatedArg

package net.kigawa.keruta.ktse.provider

import net.kigawa.keruta.ktcp.model.provider.request.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.request.ServerProviderListMsg

class ReceiveProviderListArg(
    override val msg: ServerProviderListMsg,
): ServerProviderListArg

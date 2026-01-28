package net.kigawa.keruta.ktse.provider

import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListMsg

class ReceiveProviderListArg(
    override val msg: ServerProviderListMsg,
): ServerProviderListArg

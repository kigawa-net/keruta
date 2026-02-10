package net.kigawa.keruta.ktse.provider

import net.kigawa.keruta.ktcp.model.provider.create.ServerProviderCreateArg
import net.kigawa.keruta.ktcp.model.provider.create.ServerProviderCreateMsg

class ReceiveProviderCreateArg(
    override val msg: ServerProviderCreateMsg,
): ServerProviderCreateArg

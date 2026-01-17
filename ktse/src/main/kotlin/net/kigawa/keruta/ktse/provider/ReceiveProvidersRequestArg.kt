package net.kigawa.keruta.ktse.provider

import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestArg
import net.kigawa.keruta.ktcp.model.provider.request.ServerProvidersRequestMsg

class ReceiveProvidersRequestArg(
    override val msg: ServerProvidersRequestMsg,
): ServerProvidersRequestArg

package net.kigawa.keruta.ktse.auth

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg

class ReceiveAuthRequestArg(
    override val authRequestMsg: ServerAuthRequestMsg,
): ServerAuthRequestArg

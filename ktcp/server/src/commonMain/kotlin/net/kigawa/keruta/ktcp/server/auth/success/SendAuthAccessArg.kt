package net.kigawa.keruta.ktcp.server.auth.success

import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessArg
import net.kigawa.keruta.ktcp.model.auth.sccess.ClientAuthSuccessMsg

class SendAuthAccessArg(
    override val authSuccessMsg: ClientAuthSuccessMsg,
): ClientAuthSuccessArg

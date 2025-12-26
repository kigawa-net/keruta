package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateArg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.err.GenericErrArg

interface UnknownArg {
    fun tryToGenericError(): GenericErrArg?
    fun tryToAuthenticate(): AuthenticateArg?
}

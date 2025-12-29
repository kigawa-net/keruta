package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateArg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.serialize.DecodeFrameErr
import net.kigawa.kodel.api.err.Res

interface UnknownArg {
    fun tryToGenericError(): GenericErrArg?
    fun tryToAuthenticate(): Res<AuthenticateArg, DecodeFrameErr>?
}

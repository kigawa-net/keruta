package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateArg
import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.types.DecodeFrameErr
import net.kigawa.kodel.api.err.Res

interface UnknownArg {
    fun tryToGenericError(): Res<GenericErrArg, DecodeFrameErr>?
    fun tryToAuthenticate(): Res<AuthenticateArg, DecodeFrameErr>?
}

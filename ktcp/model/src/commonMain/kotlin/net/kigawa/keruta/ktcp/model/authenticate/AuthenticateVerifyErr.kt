package net.kigawa.keruta.ktcp.model.authenticate

import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.err.KtcpErr

class AuthenticateVerifyErr(code: ErrCode): KtcpErr(code) {
}

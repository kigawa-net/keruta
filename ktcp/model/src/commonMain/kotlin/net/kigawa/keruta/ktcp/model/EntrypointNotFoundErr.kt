package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.err.KtcpErr

class EntrypointNotFoundErr(message: String): KtcpErr(ErrCode.ENTRYPOINT_NOT_FOUND,message)

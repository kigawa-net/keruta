package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

class EntrypointNotFoundErr(message: String): KtcpErr(ErrCode.ENTRYPOINT_NOT_FOUND, message)

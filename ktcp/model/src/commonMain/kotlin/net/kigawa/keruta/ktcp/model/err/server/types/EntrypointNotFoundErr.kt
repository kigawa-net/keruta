package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class EntrypointNotFoundErr(message: String, cause: Exception?): KtcpServerErr(
    ServerErrCode.ENTRYPOINT_NOT_FOUND, message, cause,
)

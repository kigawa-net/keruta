package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

class VerifyFailErr(e: Exception): VerifyErr(
    ErrCode.VERIFY_FAIL,
    cause = e,
)

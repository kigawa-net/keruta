package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrMsg

class ReceiveGenericErrArg(
    override val msg: GenericErrMsg,
): ClientGenericErrArg

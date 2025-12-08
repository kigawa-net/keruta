package net.kigawa.keruta.ktcp.model

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface KtcpUnknownMsg {
    @OptIn(ExperimentalTime::class)
    val timestamp: Instant

    fun tryToAuthenticate(): AuthenticateMsg?
}

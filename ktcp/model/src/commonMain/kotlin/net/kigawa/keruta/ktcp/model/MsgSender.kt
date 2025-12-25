package net.kigawa.keruta.ktcp.model

import kotlinx.serialization.Serializable

interface MsgSender {
    fun send(msg: @Serializable Any)
}

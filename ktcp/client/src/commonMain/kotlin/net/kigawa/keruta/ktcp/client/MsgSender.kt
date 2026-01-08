package net.kigawa.keruta.ktcp.client

import kotlinx.serialization.Serializable

interface MsgSender {
    fun sendMsg(msg: @Serializable Any)

}

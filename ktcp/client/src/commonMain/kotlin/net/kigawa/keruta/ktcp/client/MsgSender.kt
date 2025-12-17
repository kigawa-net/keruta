package net.kigawa.keruta.ktcp.client

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

interface MsgSender {
    fun sendMsg(msg: @Serializable Any)

}

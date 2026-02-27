package net.kigawa.keruta.ktcp.model.msg.server

import net.kigawa.keruta.ktcp.model.msg.KtcpMsg

interface ServerMsg: KtcpMsg {
    override val type: ServerMsgType
}

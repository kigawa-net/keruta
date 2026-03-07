package net.kigawa.keruta.ktcp.domain.msg.server

import net.kigawa.keruta.ktcp.domain.msg.KtcpMsg

interface ServerMsg: KtcpMsg {
    override val type: ServerMsgType
}

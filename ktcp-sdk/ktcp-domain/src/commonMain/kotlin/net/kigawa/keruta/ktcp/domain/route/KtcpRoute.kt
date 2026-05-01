package net.kigawa.keruta.ktcp.domain.route

import kotlinx.serialization.KSerializer
import net.kigawa.keruta.ktcp.domain.msg.KtcpMsg
import net.kigawa.keruta.ktcp.domain.msg.MsgType

interface KtcpRoute<T: KtcpMsg> {
    val type: MsgType
    val serializer: KSerializer<T>
}

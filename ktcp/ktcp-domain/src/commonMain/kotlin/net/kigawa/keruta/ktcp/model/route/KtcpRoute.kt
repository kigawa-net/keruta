package net.kigawa.keruta.ktcp.model.route

import kotlinx.serialization.KSerializer
import net.kigawa.keruta.ktcp.model.msg.KtcpMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType

interface KtcpRoute<T: KtcpMsg> {
    val type: MsgType
    val serializer: KSerializer<T>
}

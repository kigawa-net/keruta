package net.kigawa.keruta.ktse

import io.ktor.websocket.Frame
import net.kigawa.keruta.ktcp.model.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import tools.jackson.databind.JsonNode

class WebsocketUnknownMsg(
    val reader: Frame.Text,
    node: JsonNode,
): KtcpUnknownMsg {

    override fun tryToAuthenticateMsg(): AuthenticateMsg? {
        TODO("Not yet implemented")
    }
}

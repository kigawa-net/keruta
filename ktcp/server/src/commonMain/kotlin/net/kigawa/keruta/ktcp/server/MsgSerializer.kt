package net.kigawa.keruta.ktcp.server

import kotlinx.serialization.Serializable

interface MsgSerializer {
    fun serialize(msg: @Serializable Any): String
}

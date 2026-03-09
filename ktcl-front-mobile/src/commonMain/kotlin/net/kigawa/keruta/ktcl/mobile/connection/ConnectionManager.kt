package net.kigawa.keruta.ktcl.mobile.connection

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

expect class ConnectionManager(config: MobileConfig) {
    suspend fun connect(): MobileKtcpConnection
    fun disconnect()
}

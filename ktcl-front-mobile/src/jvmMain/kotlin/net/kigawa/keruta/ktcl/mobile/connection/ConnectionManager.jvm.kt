package net.kigawa.keruta.ktcl.mobile.connection

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

/**
 * JVM用のConnectionManagerスタブ実装
 * モバイル専用プロジェクトのため、JVMターゲットでは使用しない
 */
actual class ConnectionManager actual constructor(
    private val config: MobileConfig,
) {
    actual suspend fun connect(): MobileKtcpConnection {
        throw NotImplementedError("JVMターゲットではConnectionManagerは使用できません")
    }
}
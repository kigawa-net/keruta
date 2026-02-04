package net.kigawa.keruta.ktcl.mobile.connection

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

/**
 * JS用のConnectionManagerスタブ実装
 * モバイル専用プロジェクトのため、JSターゲットでは使用しない
 */
actual class ConnectionManager actual constructor(
    private val config: MobileConfig,
) {
    actual suspend fun connect(): MobileKtcpConnection {
        throw NotImplementedError("JSターゲットではConnectionManagerは使用できません")
    }
}
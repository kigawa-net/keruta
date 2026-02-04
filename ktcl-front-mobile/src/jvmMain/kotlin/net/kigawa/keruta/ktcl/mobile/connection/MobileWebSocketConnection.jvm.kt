package net.kigawa.keruta.ktcl.mobile.connection

/**
 * JVM用のMobileWebSocketConnectionスタブ実装
 * モバイル専用プロジェクトのため、JVMターゲットでは使用しない
 */
actual class MobileWebSocketConnection {
    actual suspend fun send(msg: String) {
        throw NotImplementedError("JVMターゲットではMobileWebSocketConnectionは使用できません")
    }

    actual suspend fun receive(): String? {
        throw NotImplementedError("JVMターゲットではMobileWebSocketConnectionは使用できません")
    }

    actual suspend fun close() {
        throw NotImplementedError("JVMターゲットではMobileWebSocketConnectionは使用できません")
    }
}
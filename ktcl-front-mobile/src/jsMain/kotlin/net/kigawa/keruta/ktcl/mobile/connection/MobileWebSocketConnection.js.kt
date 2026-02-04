package net.kigawa.keruta.ktcl.mobile.connection

/**
 * JS用のMobileWebSocketConnectionスタブ実装
 * モバイル専用プロジェクトのため、JSターゲットでは使用しない
 */
actual class MobileWebSocketConnection {
    actual suspend fun send(msg: String) {
        throw NotImplementedError("JSターゲットではMobileWebSocketConnectionは使用できません")
    }

    actual suspend fun receive(): String? {
        throw NotImplementedError("JSターゲットではMobileWebSocketConnectionは使用できません")
    }

    actual suspend fun close() {
        throw NotImplementedError("JSターゲットではMobileWebSocketConnectionは使用できません")
    }
}
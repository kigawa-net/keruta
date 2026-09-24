package net.kigawa.keruta.ktcl.mobile.connection

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * JS用のMobileWebSocketConnectionスタブ実装
 * モバイル専用プロジェクトのため、JSターゲットでは使用しない
 */
actual class MobileWebSocketConnection {
    actual val messages: SharedFlow<String> = MutableSharedFlow()

    actual suspend fun send(msg: String): Unit = throw NotImplementedError("JSターゲットではMobileWebSocketConnectionは使用できません")

    actual suspend fun close(): Unit = throw NotImplementedError("JSターゲットではMobileWebSocketConnectionは使用できません")
}

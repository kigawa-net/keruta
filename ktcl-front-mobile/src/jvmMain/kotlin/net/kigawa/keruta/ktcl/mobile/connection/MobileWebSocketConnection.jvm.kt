package net.kigawa.keruta.ktcl.mobile.connection

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * JVM用のMobileWebSocketConnectionスタブ実装
 * モバイル専用プロジェクトのため、JVMターゲットでは使用しない
 */
actual class MobileWebSocketConnection {
    actual val messages: SharedFlow<String> = MutableSharedFlow()

    actual suspend fun send(msg: String): Unit = throw NotImplementedError("JVMターゲットではMobileWebSocketConnectionは使用できません")

    actual suspend fun close(): Unit = throw NotImplementedError("JVMターゲットではMobileWebSocketConnectionは使用できません")
}

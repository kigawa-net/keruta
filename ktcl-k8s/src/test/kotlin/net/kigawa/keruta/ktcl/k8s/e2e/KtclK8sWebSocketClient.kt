package net.kigawa.keruta.ktcl.k8s.e2e

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 * KTCP WebSocket クライアントユーティリティ for KTCL-K8s e2eテスト
 */
class KtclK8sWebSocketClient {
    private val client = HttpClient {
        install(WebSockets)
    }

    private var session: WebSocketSession? = null
    private val receivedMessages = Channel<String>(Channel.BUFFERED)

    /**
     * WebSocketサーバーに接続
     */
    suspend fun connect(url: String) {
        session = client.webSocketSession(url)
    }

    /**
     * メッセージを送信
     */
    suspend fun send(message: String) {
        session?.send(message)
    }

    /**
     * メッセージを受信（1つ）
     */
    suspend fun receive(): String {
        return receivedMessages.receive()
    }

    /**
     * 受信ループを開始（バックグラウンド）
     */
    fun startReceiving(scope: CoroutineScope) {
        scope.launch {
            session?.incoming?.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> receivedMessages.send(frame.readText())
                    is Frame.Close -> {}
                    else -> {}
                }
            }
        }
    }

    /**
     * 接続を閉じる
     */
    suspend fun close() {
        session?.close()
        client.close()
    }
}

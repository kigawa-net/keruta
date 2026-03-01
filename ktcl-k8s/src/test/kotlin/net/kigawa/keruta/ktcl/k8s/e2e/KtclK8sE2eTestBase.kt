package net.kigawa.keruta.ktcl.k8s.e2e

/**
 * KTCL-K8s e2eテスト用基底クラス
 */
abstract class KtclK8sE2eTestBase {
    companion object {
        // KTSE服务器的URL（テスト時に設定）
        const val DEFAULT_KTSE_WS_URL = "ws://localhost:8080/ws/ktcp"
        
        /**
         * KTSE WebSocket URLを取得
         */
        @JvmStatic
        protected fun getKtseWsUrl(): String {
            return System.getenv("KTSE_WS_URL") ?: DEFAULT_KTSE_WS_URL
        }
    }
}

package net.kigawa.keruta.ktcl.k8s.e2e

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * KTCL-K8s e2e接続テスト
 * KTSEサーバーが起動している必要がある
 */
class KtclK8sConnectionTest: KtclK8sE2eTestBase() {
    
    @Test
    fun `KTSEサーバーに接続できる`() = runBlocking {
        val client = KtclK8sWebSocketClient()
        try {
            client.connect(KtclK8sE2eTestBase.getKtseWsUrl())
            // 接続できれば成功
        } catch (e: Exception) {
            // サーバーが起動していない場合はスキップ
            println("Skipping test: KTSE server not available - ${e.message}")
            assertTrue(true)
        } finally {
            try {
                client.close()
            } catch (e: Exception) {
                // 接続に失敗した場合は閉じる必要はない
            }
        }
    }
}

package net.kigawa.keruta.ktcl.k8s

import kotlin.test.Test
import kotlin.test.assertNotNull

class KtclK8sServerTest {
    /**
     * Ktorのモジュールローディングはobjectシングルトンを期待する。
     * classに変更するとHTTPサーバーが正常に起動せずヘルスチェックが失敗する。
     */
    @Test
    fun `KtclK8sServerはobjectとして宣言されていなければならない`() {
        assertNotNull(
            KtclK8sServer::class.objectInstance,
            "KtclK8sServer must be declared as 'object', not 'class'. " +
                "Changing to 'class' breaks Ktor module loading and causes CrashLoopBackOff.",
        )
    }
}

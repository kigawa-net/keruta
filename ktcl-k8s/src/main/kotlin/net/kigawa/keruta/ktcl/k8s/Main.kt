package net.kigawa.keruta.ktcl.k8s

import net.kigawa.keruta.ktcl.k8s.config.K8sConfig

object Main {
    val config = K8sConfig.fromEnvironment()
    val client = KerutaK8sClient(config)

    @JvmStatic
    fun main(args: Array<String>) {
        // EngineMainを使用してKtorサーバーを起動
        io.ktor.server.netty.EngineMain.main(args)
    }
}

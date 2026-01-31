package net.kigawa.keruta.ktcl.k8s

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.kodel.api.log.LoggerFactory

object Main {
    private val logger = LoggerFactory.get("Main")
    val config = K8sConfig.fromEnvironment()
    val client = KerutaK8sClient(config)

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            runMain()
        }
    }

    private suspend fun runMain() = coroutineScope {

        // 常にWebサーバーを起動
        logger.info { "Starting web server on port ${config.webPort}" }

        // 通常モード: WebサーバーとCLIモードを並行起動
        logger.info { "Running in CLI mode with web server" }

        // Webサーバーを非ブロッキングで起動（wait=falseなので即座に戻る）
        startWebModeNonBlocking(config)

        // CLIモード（タスク実行）を起動
        client.start()
    }

    private fun startWebModeNonBlocking(config: K8sConfig) {
        net.kigawa.keruta.ktcl.k8s.web.startWebServerNonBlocking(config.webPort)
    }
}

package net.kigawa.keruta.ktcl.k8s

import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory

object Main {
    init {
        LoggerFactory.configure {
            level = LogLevel.DEBUG
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // EngineMainを使用してKtorサーバーを起動
        io.ktor.server.netty.EngineMain.main(args)
    }
}

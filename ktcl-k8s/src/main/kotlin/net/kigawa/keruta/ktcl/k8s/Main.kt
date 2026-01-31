package net.kigawa.keruta.ktcl.k8s

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        // EngineMainを使用してKtorサーバーを起動
        io.ktor.server.netty.EngineMain.main(args)
    }
}

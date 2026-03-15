package net.kigawa.keruta.ktse

import io.ktor.server.netty.*
import kotlinx.coroutines.asCoroutineDispatcher
import net.kigawa.keruta.ktcp.server.KtseApp
import java.util.concurrent.Executors

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val coroutineContext = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors().let { if (it > 4) it - 1 else it }
        ).asCoroutineDispatcher()
        val ktseApp = KtseApp(coroutineContext)
        ktseApp.run()
        EngineMain.main(args)
    }
}

package net.kigawa.keruta.ktse

import io.ktor.server.netty.*
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcp.server.KtseApp
import java.util.concurrent.Executors

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val coroutineContext = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors().let { if (it > 4) it - 1 else it }
        ).asCoroutineDispatcher()
        val ktseApp = KtseApp(coroutineContext)
        val ktseAppJob = ktseApp.run()
        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking(coroutineContext) {
                withContext(NonCancellable) {
                    ktseAppJob.join()
                }
            }
            coroutineContext.close()
        })
        EngineMain.main(args)
    }
}

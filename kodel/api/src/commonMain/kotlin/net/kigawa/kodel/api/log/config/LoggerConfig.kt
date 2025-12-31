package net.kigawa.kodel.api.log.config

import net.kigawa.kodel.api.log.Kogger
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.config.handler.HandlerConfig
import net.kigawa.kodel.api.log.logLevel
import net.kigawa.kodel.api.log.removeAllHandlers
import kotlin.reflect.KProperty0


data class LoggerConfig(
    val level: LogLevel? = null,
    val handlerConfigs: List<HandlerConfig> = emptyList(),
) {

    fun configureLogger(loggerField: KProperty0<Kogger>) {
        configureLevel(loggerField)
        configureHandlers(loggerField)
    }

    private fun configureLevel(loggerField: KProperty0<Kogger>) {
        if (level == null) return
        loggerField.get().logLevel = level
    }

    fun configureHandlers(loggerField: KProperty0<Kogger>) {
        if (handlerConfigs.isEmpty()) return
        loggerField.get().apply {
            removeAllHandlers()
            handlerConfigs.forEach { config ->
                config.configureHandler(this)
            }
        }
    }
}

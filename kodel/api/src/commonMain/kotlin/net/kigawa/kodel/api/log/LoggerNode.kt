package net.kigawa.kodel.api.log

import net.kigawa.kodel.api.log.config.LoggerConfig
import net.kigawa.kodel.api.log.config.LoggerConfigureDsl

class LoggerNode(
    val parent: LoggerNode?,
    val section: String,
) {
    val kogger by lazy { NativeLoggerAdaptor.getKogger(name) }
    private val children = mutableMapOf<String, LoggerNode>()
    val name: String
        get() {
            if (parent == null || parent.name == "") return section
            return "${parent.name}.$section"
        }

    fun getChild(section: String) = children.getOrPut(section) { LoggerNode(this, section) }
    fun configureDsl(configDsl: LoggerConfigureDsl) {
        configureConfig(configDsl.asLoggerConfig())
        configDsl.children.forEach { (key, value) ->
            getChild(key).configureDsl(value)
        }
    }

    fun configureConfig(config: LoggerConfig) = config.configureLogger(::kogger)
}
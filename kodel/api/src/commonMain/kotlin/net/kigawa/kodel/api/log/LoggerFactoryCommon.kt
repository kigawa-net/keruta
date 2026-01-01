package net.kigawa.kodel.api.log

import net.kigawa.kodel.api.log.config.root.RootLoggerConfigureDsl
import kotlin.reflect.KClass

abstract class LoggerFactoryCommon {
    val loggerNode = LoggerNode(
        null, ""
    )

    fun get(clazz: KClass<*>): Kogger = get(clazz.simpleName!!)
    fun get(name: String): Kogger = name
        .split(".")
        .fold(loggerNode) { node, section ->
            node.getChild(section)
        }.kogger


    protected abstract fun configureRoot()
    @Suppress("unused")
    fun configure(block: RootLoggerConfigureDsl.() -> Unit) {
        RootLoggerConfigureDsl().apply(block).let {
            configureRoot()
            loggerNode.configureDsl(it)
        }
    }
}

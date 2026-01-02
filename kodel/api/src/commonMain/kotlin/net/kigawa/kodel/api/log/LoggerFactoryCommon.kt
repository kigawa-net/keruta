package net.kigawa.kodel.api.log

import net.kigawa.kodel.api.log.config.root.RootLoggerConfigureDsl
import kotlin.reflect.KClass

abstract class LoggerFactoryCommon {
    val loggerNode = LoggerNode(
        null, "", null
    )

    fun get(clazz: KClass<*>): Kogger = get(clazz.qualifiedName!!)
    fun get(name: String): Kogger = name
        .split(".")
        .filter { it.isNotBlank() }
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

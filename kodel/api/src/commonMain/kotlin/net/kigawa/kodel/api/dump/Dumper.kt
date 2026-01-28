package net.kigawa.kodel.api.dump

import kotlin.reflect.KClass
import kotlin.reflect.KProperty0

object Dumper {
    val strBuilder = StrDumpBuilder()
    fun dump(kClass: KClass<*>, vararg fields: DumpField) = DumpComponent(
        kClass.simpleName!!, fields
    )
}

infix fun <T> KProperty0<T>.with(block: (T) -> DumpComponent): DumpField = DumpField(
    this.name, block(this.get())
)

val <T> T.dump get() = DumpComponent(this.toString(), emptyArray())
val <T> List<T>.dump
    get() = DumpComponent(
        "",
        mapIndexed { index, t ->
            DumpField(index.toString(), DumpComponent(t.toString(), emptyArray()))
        }.toTypedArray()
    )

infix fun <T> KProperty0<T>.withStr(block: (T) -> String): DumpField = DumpField(
    this.name, DumpComponent(block(this.get()), emptyArray())
)

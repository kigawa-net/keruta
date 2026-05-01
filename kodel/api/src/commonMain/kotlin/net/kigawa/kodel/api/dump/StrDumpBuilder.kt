package net.kigawa.kodel.api.dump

class StrDumpBuilder {
    fun build(dumpComponent: DumpComponent): String = buildToBuffer(
        dumpComponent, ""
    ).toString()


    fun buildToBuffer(dumpComponent: DumpComponent, indent: String): StringBuilder {
        val builder = StringBuilder(64)
        builder.append(dumpComponent.name)
        if (dumpComponent.children.isEmpty()) return builder.appendLine()
        builder.appendLine("(")
        val childIndent = "$indent    "
        dumpComponent.children.forEach {
            builder.append(childIndent).append(it.name).append("=")
                .append(buildToBuffer(it.component, childIndent))
        }
        builder.append(indent).appendLine(")")
        return builder
    }
}

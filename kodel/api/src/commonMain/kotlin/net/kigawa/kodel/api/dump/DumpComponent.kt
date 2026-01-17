package net.kigawa.kodel.api.dump

class DumpComponent(
    val name: String,
    val children: Array<out DumpField>,
) {
    fun str() = Dumper.strBuilder.build(this)
    override fun toString(): String = str()
}


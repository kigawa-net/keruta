package net.kigawa.keruta.ktcl.web

import dev.fritz2.core.render

fun main() {
    render("#target") { // using id selector here, leave blank to use document.body by default
        h1 { +"My App" }
        div("some-fix-css-class") {
            p(id = "someId") {
                +"Hello World!"
            }
        }
    }
}

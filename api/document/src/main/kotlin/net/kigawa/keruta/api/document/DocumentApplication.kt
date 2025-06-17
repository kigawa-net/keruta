package net.kigawa.keruta.api.document

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["net.kigawa.keruta"])
class DocumentApplication

fun main(args: Array<String>) {
    runApplication<DocumentApplication>(*args)
}
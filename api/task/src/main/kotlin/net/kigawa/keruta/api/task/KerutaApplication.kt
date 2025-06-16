package net.kigawa.keruta.api.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["net.kigawa.keruta"])
class KerutaApplication

fun main(args: Array<String>) {
    runApplication<KerutaApplication>(*args)
}
package net.kigawa.keruta

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KerutaApplication

fun main(args: Array<String>) {
    runApplication<KerutaApplication>(*args)
}
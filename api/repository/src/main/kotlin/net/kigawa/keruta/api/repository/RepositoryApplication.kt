package net.kigawa.keruta.api.repository

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["net.kigawa.keruta"])
class RepositoryApplication

fun main(args: Array<String>) {
    runApplication<RepositoryApplication>(*args)
}
package net.kigawa.keruta.infra.persistence.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["net.kigawa.keruta.infra.persistence.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {
    
    override fun getDatabaseName(): String {
        return "keruta"
    }
}
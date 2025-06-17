package net.kigawa.keruta.infra.persistence.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["net.kigawa.keruta.infra.persistence.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return "keruta"
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://admin:password@localhost:27017/keruta?authSource=admin")

        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()

        return MongoClients.create(clientSettings)
    }
}

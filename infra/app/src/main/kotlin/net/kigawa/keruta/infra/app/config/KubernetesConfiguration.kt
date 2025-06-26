package net.kigawa.keruta.infra.app.config

import net.kigawa.keruta.core.domain.model.KubernetesConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KubernetesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "keruta.kubernetes")
    fun kubernetesConfig(): KubernetesConfig {
        return KubernetesConfig()
    }
}
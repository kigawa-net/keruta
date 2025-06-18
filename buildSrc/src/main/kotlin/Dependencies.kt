object Versions {
    const val springBoot = "3.2.0"
    const val springDependencyManagement = "1.1.4"
    const val kotlin = "1.9.20"
    const val mongodbDriver = "4.11.1"
    const val jjwt = "0.11.5"
    const val jakartaServlet = "6.0.0"
    const val springdoc = "2.3.0"
    const val thymeleaf = "3.1.2.RELEASE"
    const val keycloak = "23.0.3"
}

object Dependencies {
    // Spring
    const val springBootStarter = "org.springframework.boot:spring-boot-starter"
    const val springBootStarterWeb = "org.springframework.boot:spring-boot-starter-web"
    const val springBootStarterSecurity = "org.springframework.boot:spring-boot-starter-security"
    const val springBootStarterData = "org.springframework.boot:spring-boot-starter-data-mongodb"
    const val springBootStarterTest = "org.springframework.boot:spring-boot-starter-test"
    const val springBootStarterOauth2Client = "org.springframework.boot:spring-boot-starter-oauth2-client"

    // MongoDB
    const val mongodbDriver = "org.mongodb:mongodb-driver-sync:${Versions.mongodbDriver}"

    // JWT
    const val jjwtApi = "io.jsonwebtoken:jjwt-api:${Versions.jjwt}"
    const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:${Versions.jjwt}"
    const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}"

    // Kotlin
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib"

    // Jakarta
    const val jakartaServletApi = "jakarta.servlet:jakarta.servlet-api:${Versions.jakartaServlet}"

    // Springdoc OpenAPI (Swagger)
    const val springdocOpenApi = "org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springdoc}"

    // Thymeleaf
    const val springBootStarterThymeleaf = "org.springframework.boot:spring-boot-starter-thymeleaf"

    // Keycloak
    const val keycloakSpringBootAdapter = "org.keycloak:keycloak-spring-boot-starter:${Versions.keycloak}"
    const val keycloakSpringSecurityAdapter = "org.keycloak:keycloak-spring-security-adapter:${Versions.keycloak}"
}

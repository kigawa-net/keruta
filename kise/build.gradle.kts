plugins {
    id("kmp")
    id("serialize")
}

kotlin {
    jvm()
    sourceSets["commonMain"].dependencies {
        api(project(":kise:domain"))
        api(project(":kise:usecase"))
        api(project(":kodel:api"))
        api(project(":kodel:coroutine"))
    }
    sourceSets["jvmMain"].dependencies {
        implementation("io.ktor:ktor-server-websockets-jvm:3.4.3")
        implementation("io.ktor:ktor-server-core-jvm:3.4.3")
        implementation("com.auth0:java-jwt:4.5.2")
        api(project(":ktcp-sdk:ktcp-domain:ktcp-domain-server"))
        api(project(":ktcp-sdk:ktcp-domain"))
        // Database
        implementation("org.jetbrains.exposed:exposed-core:0.61.0")
        implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
        implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.61.0")
        implementation("com.mysql:mysql-connector-j:9.7.0")
        implementation("com.zaxxer:HikariCP:7.0.2")
    }
}
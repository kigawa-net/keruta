plugins {
    id("common")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:model"))
        // https://mvnrepository.com/artifact/com.auth0/java-jwt
        implementation("com.auth0:java-jwt:4.5.0")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
//        implementation("io.ktor:ktor-server-core")
//        implementation("io.ktor:ktor-server-websockets")
//        implementation("io.ktor:ktor-server-auth")
//        implementation("io.ktor:ktor-server-auth-jwt")
//        implementation("io.ktor:ktor-server-netty")
//        implementation("ch.qos.logback:logback-classic")
//        implementation("io.ktor:ktor-server-config-yaml")
    }
    sourceSets["commonTest"].dependencies {
//        implementation("io.ktor:ktor-server-test-host")
//        implementation("org.jetbrains.kotlin:kotlin-test-junit")
    }
    sourceSets["jvmMain"].dependencies {}
}

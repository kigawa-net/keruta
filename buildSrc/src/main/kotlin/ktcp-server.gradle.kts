plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:model"))
        api(project(":kodel:coroutine"))
        // https://mvnrepository.com/artifact/com.auth0/java-jwt
        implementation("com.auth0:java-jwt:4.5.0")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}

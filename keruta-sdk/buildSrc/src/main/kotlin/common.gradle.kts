plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}
val coroutineVersion = "1.10.2"
dependencies {
// https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
// https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-reactor
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    // https://mvnrepository.com/artifact/net.kigawa.kutil/kutil
    implementation("net.kigawa.kutil:kutil:4.0.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

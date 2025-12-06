plugins {
    kotlin("multiplatform")
}

dependencies {
//    implementation(project(":kodel:api"))
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
//
//    testImplementation(kotlin("test"))
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}
kotlin {
    js {
        browser {
        }
    }
    jvm {}
    sourceSets["commonMain"].dependencies {
        implementation(project(":kodel:api"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    }
    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    }
    sourceSets["jvmMain"].dependencies {}
    sourceSets["jsMain"].dependencies {
        // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-js
        implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${Version.kotlinVersion}")
    }
}

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:usecase"))
    implementation(project(":infra:persistence"))
    implementation(project(":infra:security"))
    implementation(project(":infra:app"))

    implementation(Dependencies.kotlinStdlib)
    implementation(Dependencies.kotlinReflect)
    implementation(Dependencies.springBootStarter)
    implementation(Dependencies.springBootStarterWeb)
    implementation(Dependencies.springBootStarterWebsocket)
    implementation(Dependencies.springBootStarterSecurity)

    // Swagger/OpenAPI
    implementation(Dependencies.springdocOpenApi)

    // Thymeleaf
    implementation(Dependencies.springBootStarterThymeleaf)

    // MongoDB
    implementation(Dependencies.springBootStarterData)
    implementation(Dependencies.mongodbDriver)

    testImplementation(Dependencies.springBootStarterTest)
    testImplementation(Dependencies.testcontainersJunit)
    testImplementation(Dependencies.testcontainersCore)
    testImplementation(Dependencies.testcontainersMongodb)
    testImplementation(Dependencies.testcontainersPostgresql)
}

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:domain"))
    
    implementation(Dependencies.kotlinStdlib)
    implementation(Dependencies.kotlinReflect)
    implementation(Dependencies.springBootStarter)
    
    testImplementation(Dependencies.springBootStarterTest)
}
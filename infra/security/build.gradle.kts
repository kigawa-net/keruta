plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:usecase"))
    
    implementation(Dependencies.kotlinStdlib)
    implementation(Dependencies.kotlinReflect)
    implementation(Dependencies.springBootStarter)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.jjwtApi)
    implementation(Dependencies.jjwtImpl)
    implementation(Dependencies.jjwtJackson)
    
    testImplementation(Dependencies.springBootStarterTest)
}
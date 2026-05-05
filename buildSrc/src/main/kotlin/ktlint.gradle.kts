plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    version.set("1.5.0")
    android.set(false)
    outputToConsole.set(true)
    coloredOutput.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

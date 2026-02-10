plugins {
    id("compose-mobile-lib")
}

val iosSimulatorName = project.findProperty("iosSimulator")?.toString() ?: "iPhone 17 Pro"

tasks.register<Exec>("iosSimulatorBuild") {
    group = "build"
    description = "Build the iOS app for simulator"
    dependsOn("linkDebugFrameworkIosSimulatorArm64")

    workingDir(projectDir.resolve("iosApp"))
    commandLine(
        "xcodebuild",
        "-project", "iosApp.xcodeproj",
        "-scheme", "iosApp",
        "-configuration", "Debug",
        "-destination", "platform=iOS Simulator,name=$iosSimulatorName",
        "-derivedDataPath", "build",
        "build"
    )
}

tasks.register("iosSimulatorRun") {
    group = "run"
    description = "Build and run the iOS app on simulator"
    dependsOn("iosSimulatorBuild")

    doLast {
        val simulatorName = iosSimulatorName

        // Get simulator UDID
        val listProcess = ProcessBuilder("xcrun", "simctl", "list", "devices", "available", "-j")
            .redirectErrorStream(true)
            .start()
        val jsonText = listProcess.inputStream.bufferedReader().readText()
        listProcess.waitFor()

        val json = groovy.json.JsonSlurper().parseText(jsonText) as Map<*, *>
        val devices = json["devices"] as Map<*, *>
        var udid: String? = null
        devices.values.forEach { deviceList ->
            (deviceList as List<*>).forEach { device ->
                val d = device as Map<*, *>
                if (d["name"] == simulatorName && d["isAvailable"] == true) {
                    udid = d["udid"] as String
                }
            }
        }
        requireNotNull(udid) { "Simulator '$simulatorName' not found" }

        // Boot simulator (ignore error if already booted)
        ProcessBuilder("xcrun", "simctl", "boot", udid)
            .redirectErrorStream(true)
            .start()
            .waitFor()

        // Open Simulator app
        ProcessBuilder("open", "-a", "Simulator")
            .start()
            .waitFor()

        // Install and launch
        val appPath = projectDir.resolve("iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app")
        val installProcess = ProcessBuilder("xcrun", "simctl", "install", udid, appPath.absolutePath)
            .redirectErrorStream(true)
            .start()
        println(installProcess.inputStream.bufferedReader().readText())
        val installResult = installProcess.waitFor()
        if (installResult != 0) {
            throw GradleException("Failed to install app")
        }

        val launchProcess = ProcessBuilder("xcrun", "simctl", "launch", udid, "net.kigawa.keruta.ktcl.mobile")
            .redirectErrorStream(true)
            .start()
        println(launchProcess.inputStream.bufferedReader().readText())
        val launchResult = launchProcess.waitFor()
        if (launchResult != 0) {
            throw GradleException("Failed to launch app")
        }

        println("App launched on $simulatorName")
    }
}
kotlin {
    jvmToolchain(25)
}

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}
object Conf {
    const val GROUP = "net.kigawa"
    // Base version - will be modified based on branch name if available
    const val BASE_VERSION = "0.0.1"
}

// Determine version based on branch name
// Branch naming convention for releases: release/vx.x.x (e.g., release/v1.0.0)
fun determineVersion(): String {
    // Try to get branch name from different environment variables
    // For pull requests, GITHUB_HEAD_REF contains the source branch name
    // For direct pushes, GITHUB_REF_NAME contains the branch name
    val branchName = System.getenv("GITHUB_HEAD_REF")
        ?: System.getenv("GITHUB_REF_NAME")
        ?: return Conf.BASE_VERSION

    // For main branch, use the base version
    if (branchName == "main") {
        return Conf.BASE_VERSION
    }

    // For release branches in format 'release/vx.x.x', use the version from the branch name
    val releasePattern = Regex("^release/v(\\d+\\.\\d+\\.\\d+)$")
    val matchResult = releasePattern.find(branchName)
    if (matchResult != null) {
        // Extract version number from branch name
        val versionFromBranch = matchResult.groupValues[1]
        return versionFromBranch
    }

    // For other branches, use format: baseVersion-branchName-SNAPSHOT
    // Replace any non-alphanumeric characters with dashes for Maven compatibility
    val sanitizedBranchName = branchName.replace(Regex("[^a-zA-Z0-9]"), "-")
    return "${Conf.BASE_VERSION}-${sanitizedBranchName}-SNAPSHOT"
}

val projectVersion = determineVersion()

group = Conf.GROUP
version = projectVersion
allprojects {
    apply(plugin = "common")
    group = Conf.GROUP
    version = projectVersion
}

// Add a task to print the version (useful for debugging)
tasks.register("printVersion") {
    doLast {
        val branchName = System.getenv("GITHUB_HEAD_REF") ?: System.getenv("GITHUB_REF_NAME") ?: "unknown"
        println("Project version: $version")
        println("Determined from branch: $branchName")

        // Check if it's a release branch
        val releasePattern = Regex("^release/v(\\d+\\.\\d+\\.\\d+)$")
        val isReleaseBranch = releasePattern.matches(branchName)
        println("Is release branch format (release/vx.x.x): $isReleaseBranch")

        if (isReleaseBranch) {
            val versionFromBranch = releasePattern.find(branchName)?.groupValues?.get(1)
            println("Version extracted from branch name: $versionFromBranch")
        }
    }
}

nexusPublishing {
    // Configure maven central repository
    // https://github.com/gradle-nexus/publish-plugin#publishing-to-maven-central-via-sonatype-ossrh
    repositories {
        sonatype()

    }
}

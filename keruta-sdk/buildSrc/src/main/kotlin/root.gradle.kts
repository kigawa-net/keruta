plugins {
    id("common")
    id("publish")
}

allprojects {
    group = "net.kigawa.keruta"
    apply(plugin = "common")
}

val projectVersion = determineVersion()
group = Conf.GROUP
version = projectVersion
allprojects {
    apply(plugin = "common")
    group = Conf.GROUP
    version = projectVersion
}

object Conf {
    const val GROUP = "net.kigawa"

    // Base version - will be modified based on branch name if available
    const val BASE_VERSION = "0.0.1"
}

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

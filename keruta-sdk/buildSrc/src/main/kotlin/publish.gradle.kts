
plugins {
    id("io.github.gradle-nexus.publish-plugin")
    `maven-publish`
    signing
}

nexusPublishing {
    // Configure maven central repository
    // https://github.com/gradle-nexus/publish-plugin#publishing-to-maven-central-via-sonatype-ossrh
    repositories {
        sonatype()

    }
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        pom {
            name = "renlin-router"
            description = "routing library for renlin"
            url = "https://github.com/Code-Sakura/renlin-router"
            properties = mapOf(
            )
            licenses {
                license {
                    name.set("MIT License")
                    url.set("http://www.opensource.org/licenses/mit-license.php")
                }
            }
            developers {
                developer {
                    id.set("net.kigawa")
                    name.set("kigawa")
                    email.set("contact@kigawa.net")
                }
                developer {
                    id.set("io.github.seizavl")
                    name.set("seizavl")
                    email.set("")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/Code-Sakura/renlin-router.git")
                developerConnection.set("scm:git:https://github.com/Code-Sakura/renlin-router.git")
                url.set("https://github.com/Code-Sakura/renlin-router")
            }
        }
    }
}

signing {
    if (project.hasProperty("mavenCentralUsername") ||
        System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null
    ) {
        useGpgCmd()
    }
}

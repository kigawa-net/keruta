
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    js {
        binaries.library()
        generateTypeScriptDefinitions()
        compilations["main"].packageJson {
            customField("types", "keruta-kicl-kicl-domain.d.ts")
        }
    }
    sourceSets["commonMain"].dependencies {
        api(project(":kodel:api"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}

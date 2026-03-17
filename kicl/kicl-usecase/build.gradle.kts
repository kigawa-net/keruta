
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    js {
        binaries.library()
        generateTypeScriptDefinitions()
        compilations["main"].packageJson {
            customField("types", "keruta-kicl-kicl-usecase.d.ts")
        }
    }
    sourceSets["commonMain"].dependencies {
        api(project(":kicl:kicl-domain"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}

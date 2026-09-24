
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    js {
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            moduleKind = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_ES
        }
        compilations["main"].packageJson {
            customField("type", "module")
            customField("types", "keruta-kicl-kicl-usecase.d.mts")
        }
    }
    sourceSets["commonMain"].dependencies {
        api(project(":kicl:kicl-domain"))
        api(project(":kicp:kicp-usecase"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
    sourceSets["jsMain"].dependencies {
        implementation("io.ktor:ktor-client-js:${Version.KTOR}")
        implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
        implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
    }
}

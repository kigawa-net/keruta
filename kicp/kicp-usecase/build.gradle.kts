
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
            customField("types", "keruta-kicp-kicp-usecase.d.mts")
        }
    }
    sourceSets["commonMain"].dependencies {
        api(project(":kicp:kicp-domain"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}


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
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}

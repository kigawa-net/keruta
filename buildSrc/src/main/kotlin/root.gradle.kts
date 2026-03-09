plugins {
    id("kmp")
}
allprojects {
    group = "net.kigawa.keruta"
    version = System.getenv("VERSION") ?: "dev"
}


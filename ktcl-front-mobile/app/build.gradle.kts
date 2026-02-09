plugins {
    id("compose-mobile-app")
}

dependencies {
    implementation(project(":ktcl-front-mobile"))
    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("net.openid:appauth:0.11.1")
}

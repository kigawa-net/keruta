plugins {
    id("compose-mobile-app")
}

dependencies {
    implementation(project(":ktcl-front-mobile"))
    implementation("androidx.activity:activity-compose:${Version.ACTIVITY_COMPOSE}")
    implementation("net.openid:appauth:${Version.APP_AUTH}")
}

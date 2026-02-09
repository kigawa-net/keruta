package net.kigawa.keruta.ktcl.mobile.di

import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

class IosAppContainer(
    config: MobileConfig = MobileConfig.default(),
) : AppContainer(config) {

    fun initialize() {
        // iOS initialization - currently no-op
    }
}

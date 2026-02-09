package net.kigawa.keruta.ktcl.mobile.di

import android.content.Context
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

class AndroidAppContainer(
    private val context: Context,
    config: MobileConfig = MobileConfig.default(),
) : AppContainer(config) {

    fun initialize() {
        oidcAuthManager.initialize(context)
        secureStorage.initialize(context)
    }
}

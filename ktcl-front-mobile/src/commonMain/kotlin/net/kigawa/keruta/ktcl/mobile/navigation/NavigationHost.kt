package net.kigawa.keruta.ktcl.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun NavigationHost(
    controller: NavigationController,
    content: @Composable (Screen) -> Unit,
) {
    val currentScreen by controller.currentScreen.collectAsState()
    content(currentScreen)
}

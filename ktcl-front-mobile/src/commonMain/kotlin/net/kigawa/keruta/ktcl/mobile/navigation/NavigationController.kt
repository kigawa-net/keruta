package net.kigawa.keruta.ktcl.mobile.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationController(
    initialScreen: Screen = Screen.Login,
) {
    private val _currentScreen = MutableStateFlow(initialScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigate(screen: Screen) {
        backStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.isEmpty()) {
            return false
        }
        _currentScreen.value = backStack.removeLast()
        return true
    }

    fun navigateToRoot(screen: Screen) {
        backStack.clear()
        _currentScreen.value = screen
    }
}

package net.kigawa.keruta.ktcl.mobile.navigation

sealed class Screen {
    data object Login : Screen()
    data object QueueList : Screen()
    data object QueueCreate : Screen()
    data class QueueDetail(val queueId: Long) : Screen()
    data object ProviderList : Screen()
}

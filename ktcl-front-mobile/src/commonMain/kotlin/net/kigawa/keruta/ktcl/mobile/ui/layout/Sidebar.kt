package net.kigawa.keruta.ktcl.mobile.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kigawa.keruta.ktcl.mobile.navigation.Screen

data class SidebarItem(
    val screen: Screen,
    val label: String,
)

@Composable
fun Sidebar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        SidebarItem(Screen.QueueList, "キュー"),
        SidebarItem(Screen.ProviderList, "プロバイダー"),
    )

    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
    ) {
        Text(
            text = "Keruta",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(24.dp))

        items.forEach { item ->
            val isSelected = when {
                item.screen == Screen.QueueList && currentScreen is Screen.QueueList -> true
                item.screen == Screen.QueueList && currentScreen is Screen.QueueCreate -> true
                item.screen == Screen.QueueList && currentScreen is Screen.QueueDetail -> true
                item.screen == Screen.ProviderList && currentScreen is Screen.ProviderList -> true
                item.screen == Screen.ProviderList && currentScreen is Screen.ProviderCreate -> true
                else -> false
            }

            SidebarMenuItem(
                label = item.label,
                isSelected = isSelected,
                onClick = { onNavigate(item.screen) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        SidebarMenuItem(
            label = "ログアウト",
            isSelected = false,
            onClick = onLogout,
        )
    }
}

@Composable
private fun SidebarMenuItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

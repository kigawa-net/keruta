package net.kigawa.keruta.ktcl.mobile.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.ui.components.ErrorMessage
import net.kigawa.keruta.ktcl.mobile.ui.components.LoadingIndicator
import net.kigawa.keruta.ktcl.mobile.ui.components.PrimaryButton
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueListViewModel

@Composable
fun QueueListScreen(
    viewModel: QueueListViewModel,
    currentScreen: Screen,
    onQueueClick: (Long) -> Unit,
    onCreateQueueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(currentScreen) {
        viewModel.loadQueues()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "キュー一覧",
                style = MaterialTheme.typography.headlineMedium,
            )
            PrimaryButton(
                text = "新規作成",
                onClick = onCreateQueueClick,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.errorMessage?.let { error ->
            ErrorMessage(message = error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            state.isLoading -> {
                LoadingIndicator()
            }
            state.queues.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "キューがありません",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.queues) { queue ->
                        QueueCard(
                            queue = queue,
                            onClick = { onQueueClick(queue.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueCard(
    queue: Queue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = queue.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${queue.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

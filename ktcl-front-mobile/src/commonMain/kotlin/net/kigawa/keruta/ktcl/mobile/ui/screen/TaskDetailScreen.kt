package net.kigawa.keruta.ktcl.mobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.ui.components.ErrorMessage
import net.kigawa.keruta.ktcl.mobile.ui.components.FormTextField
import net.kigawa.keruta.ktcl.mobile.ui.components.LoadingIndicator
import net.kigawa.keruta.ktcl.mobile.ui.components.PrimaryButton
import net.kigawa.keruta.ktcl.mobile.ui.components.SecondaryButton
import net.kigawa.keruta.ktcl.mobile.ui.components.StatusBadge
import net.kigawa.keruta.ktcl.mobile.viewmodel.TaskDetailViewModel

@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    currentScreen: Screen,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(currentScreen) {
        if (currentScreen is Screen.TaskDetail) {
            viewModel.setTaskId(currentScreen.taskId, currentScreen.queueId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "タスク詳細",
                style = MaterialTheme.typography.headlineMedium,
            )
            SecondaryButton(
                text = "戻る",
                onClick = onBack,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.errorMessage?.let { error ->
            ErrorMessage(message = error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.isLoading) {
            LoadingIndicator()
        } else if (state.task == null) {
            Text(
                text = "タスクが見つかりません",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        } else {
            // タスク情報カード
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    // ステータス
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "ステータス",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        StatusBadge(status = state.task!!.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // タイトル編集
                    FormTextField(
                        value = state.editTitle,
                        onValueChange = viewModel::setEditTitle,
                        label = "タイトル",
                        placeholder = "タスクのタイトル",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 説明編集
                    FormTextField(
                        value = state.editDescription,
                        onValueChange = viewModel::setEditDescription,
                        label = "説明",
                        placeholder = "タスクの説明",
                        singleLine = false,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 保存ボタン
                    PrimaryButton(
                        text = "保存",
                        onClick = viewModel::saveTask,
                        isLoading = state.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ステータス変更
            Text(
                text = "ステータス変更",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusButton(
                    text = "未着手",
                    isSelected = state.task!!.status == "pending",
                    onClick = { viewModel.updateStatus("pending") },
                    modifier = Modifier.weight(1f),
                )
                StatusButton(
                    text = "進行中",
                    isSelected = state.task!!.status == "in_progress",
                    onClick = { viewModel.updateStatus("in_progress") },
                    modifier = Modifier.weight(1f),
                )
                StatusButton(
                    text = "完了",
                    isSelected = state.task!!.status == "completed",
                    onClick = { viewModel.updateStatus("completed") },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
        )
    } else {
        SecondaryButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
        )
    }
}

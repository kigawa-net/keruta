package net.kigawa.keruta.ktcl.mobile.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kigawa.keruta.ktcl.mobile.ui.components.DropdownItem
import net.kigawa.keruta.ktcl.mobile.ui.components.ErrorMessage
import net.kigawa.keruta.ktcl.mobile.ui.components.FormDropdown
import net.kigawa.keruta.ktcl.mobile.ui.components.FormTextField
import net.kigawa.keruta.ktcl.mobile.ui.components.PrimaryButton
import net.kigawa.keruta.ktcl.mobile.ui.components.SecondaryButton
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueCreateViewModel

@Composable
fun QueueCreateScreen(
    viewModel: QueueCreateViewModel,
    onCreated: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProviders()
    }

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            viewModel.resetCreatedState()
            onCreated()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "キュー作成",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(24.dp))

        state.errorMessage?.let { error ->
            ErrorMessage(message = error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        FormTextField(
            value = state.name,
            onValueChange = viewModel::setName,
            label = "キュー名",
            placeholder = "キュー名を入力",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        val providerItems = state.providers.map { provider ->
            DropdownItem(
                value = provider.id,
                label = provider.name,
            )
        }

        FormDropdown(
            items = providerItems,
            selectedItem = state.selectedProviderId,
            onItemSelected = viewModel::setSelectedProvider,
            label = "プロバイダー",
            placeholder = "プロバイダーを選択",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            SecondaryButton(
                text = "キャンセル",
                onClick = onCancel,
            )
            Spacer(modifier = Modifier.width(16.dp))
            PrimaryButton(
                text = "作成",
                onClick = viewModel::createQueue,
                isLoading = state.isLoading,
            )
        }
    }
}

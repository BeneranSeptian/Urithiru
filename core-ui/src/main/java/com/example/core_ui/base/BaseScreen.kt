package com.example.core_ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.component.CircularProgressDialog

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    onBack: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val baseScreenUiState = viewModel.baseScreenUiState.collectAsStateWithLifecycle()

    if (baseScreenUiState.value.showCentralLoading) {
        CircularProgressDialog()
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ){
        content()
    }
}
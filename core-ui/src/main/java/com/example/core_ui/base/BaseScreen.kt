package com.example.core_ui.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.component.CircularProgressDialog
import com.septianbeneran.template.core.base.BaseViewModel

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    val showCentralLoading = viewModel.isCentralLoading.collectAsStateWithLifecycle()
    if (showCentralLoading.value) {
        CircularProgressDialog()
    }

    content()
}
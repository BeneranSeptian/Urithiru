package com.example.core_ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ){
        content()
    }
}
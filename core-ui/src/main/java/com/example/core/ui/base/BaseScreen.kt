package com.example.core.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.ui.component.CircularProgressDialog
import com.example.core.ui.util.permission.permissionStateHandler

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    onBack: (() -> Unit)? = null,
    content: @Composable (properties: BaseScreenProperties) -> Unit
) {
    val context = LocalContext.current
    val baseScreenUiState = viewModel.baseScreenUiState.collectAsStateWithLifecycle()

    val permissionLauncher = permissionStateHandler(
        permissionHandler = viewModel.permissionHandler,
        context = context
    )

    val properties = remember {
        BaseScreenProperties(
            handlePermission = { permissionSet, onPermissionResult ->
                viewModel.permissionHandler.handlePermission(
                    context = context,
                    permissionLauncher = permissionLauncher,
                    onPermissionResult = onPermissionResult,
                    permissionSet = permissionSet
                )
            }
        )
    }

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
    ) {
        content(properties)
    }
}
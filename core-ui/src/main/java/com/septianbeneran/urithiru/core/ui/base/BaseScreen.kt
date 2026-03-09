package com.septianbeneran.urithiru.core.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    viewModel: BaseViewModel,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    onBack: (() -> Unit)? = null,
    content: @Composable (properties: BaseScreenProperties) -> Unit
) {
    val context = LocalContext.current
    val baseScreenUiState = viewModel.baseScreenUiState.collectAsStateWithLifecycle()

    val permissionLauncher =
        _root_ide_package_.com.septianbeneran.urithiru.core.ui.util.permission.permissionStateHandler(
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
        _root_ide_package_.com.septianbeneran.urithiru.core.ui.component.CircularProgressDialog()
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        content(properties)
    }
}
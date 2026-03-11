package com.septianbeneran.urithiru.core.ui.base

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.ui.component.CentralLoadingDialog
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBar
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.TopAppBarArgs
import com.septianbeneran.urithiru.core.ui.util.permission.permissionStateHandler

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    viewModel: BaseViewModel,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    isUseSystembarsPadding: Boolean = true,
    topAppBarArgs: TopAppBarArgs? = null,
    onBack: (() -> Unit)? = null,
    content: @Composable (properties: BaseScreenProperties) -> Unit
) {
    val context = LocalContext.current
    val baseScreenUiState = viewModel.baseScreenUiState.collectAsStateWithLifecycle()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val permissionLauncher =
        permissionStateHandler(
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
        CentralLoadingDialog()
    }

    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .then(if (isUseSystembarsPadding) Modifier.systemBarsPadding() else Modifier)
    ) {
        topAppBarArgs?.let {
            UriTopAppBar(
                topAppBarArgs = it,
                onLeadingComposableClick = { backDispatcher?.onBackPressed() }
            )
        }
        content(properties)
    }
}
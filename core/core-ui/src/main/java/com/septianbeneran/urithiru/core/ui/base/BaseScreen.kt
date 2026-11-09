package com.septianbeneran.urithiru.core.ui.base

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.ui.component.CentralLoadingDialog
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBar
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.TopAppBarArgs
import com.septianbeneran.urithiru.core.ui.util.permission.permissionStateHandler

/**
 * The frame every screen sits in. By default the content stays clear of the system bars and of anything
 * floating over the bottom of the screen ([LocalBottomOverlayHeight]). With [drawBehindBottomOverlay] the
 * content runs to the bottom edge, behind the floating bar, and must add [LocalScreenBottomPadding] to its
 * scrolling list's `contentPadding` so the last item can still scroll into view.
 */
@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    viewModel: BaseViewModel,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    isUseSystembarsPadding: Boolean = true,
    drawBehindBottomOverlay: Boolean = false,
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

    val layoutDirection = LocalLayoutDirection.current
    val systemBarsBottom = if (isUseSystembarsPadding) {
        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    } else {
        0.dp
    }
    val bottomPadding = contentPadding.calculateBottomPadding() + systemBarsBottom + LocalBottomOverlayHeight.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                top = contentPadding.calculateTopPadding(),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = if (drawBehindBottomOverlay) 0.dp else bottomPadding
            )
            .then(
                if (isUseSystembarsPadding) {
                    Modifier.windowInsetsPadding(WindowInsets.systemBars.only(Horizontal + Top))
                } else {
                    Modifier
                }
            )
    ) {
        topAppBarArgs?.let {
            UriTopAppBar(
                topAppBarArgs = it,
                onLeadingComposableClick = { backDispatcher?.onBackPressed() }
            )
        }
        CompositionLocalProvider(LocalScreenBottomPadding provides if (drawBehindBottomOverlay) bottomPadding else 0.dp) {
            content(properties)
        }
    }
}
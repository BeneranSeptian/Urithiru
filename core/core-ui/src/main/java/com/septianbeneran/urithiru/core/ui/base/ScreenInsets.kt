package com.septianbeneran.urithiru.core.ui.base

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How much UI floats over the bottom of the screen, above the system navigation bar (the app's floating
 * bottom bar, for example). Provided by `:app`; screens never set it. [BaseScreen] keeps its content clear
 * of it, or hands it to the content through [LocalScreenBottomPadding].
 */
val LocalBottomOverlayHeight = compositionLocalOf { 0.dp }

/**
 * Bottom padding a scrolling list should add to its `contentPadding` so its last item can scroll clear of
 * the system navigation bar and anything floating over it. Non-zero only inside a [BaseScreen] that draws
 * behind the bottom overlay; elsewhere it's 0, so it's always safe to add.
 */
val LocalScreenBottomPadding = compositionLocalOf<Dp> { 0.dp }

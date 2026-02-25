package com.example.core.ui.util

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.ui.util.ScreenTransition.Default
import com.example.core.ui.util.ScreenTransition.Fade
import com.example.core.ui.util.ScreenTransition.None
import com.example.core.ui.util.ScreenTransition.Scale

fun NavGraphBuilder.baseComposable(
    route: String,
    transition: ScreenTransition = Default,
    content: @Composable () -> Unit
) {
    composable(
        route = route,
        enterTransition = {
            when (transition) {
                Fade -> fadeIn()
                Scale -> scaleIn()
                None -> EnterTransition.None
                else -> slideIntoContainer(
                    towards = Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        },
        exitTransition = {
            when (transition) {
                Fade -> fadeOut()
                Scale -> fadeOut()
                None -> ExitTransition.None
                else -> slideOutOfContainer(
                    towards = Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        }
    ) {
        content()
    }
}
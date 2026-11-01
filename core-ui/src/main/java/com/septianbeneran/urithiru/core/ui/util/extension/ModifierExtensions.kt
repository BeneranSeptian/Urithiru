package com.septianbeneran.urithiru.core.ui.util.extension

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800), repeatMode = RepeatMode.Restart
        ), label = "ShimmerTranslate"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        ),
        shape = RoundedCornerShape(4.dp)
    )
}

/** Widens the element by [horizontal] on both sides, e.g. so a row inside padded content reaches the screen edges. */
fun Modifier.bleed(horizontal: Dp): Modifier = layout { measurable, constraints ->
    val extra = horizontal.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.minWidth + extra * 2,
            maxWidth = constraints.maxWidth + extra * 2
        )
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-extra, 0)
    }
}

/** Shrinks pager pages slightly as they move away from the centre. */
fun Modifier.pageScale(pagerState: PagerState, page: Int): Modifier = graphicsLayer {
    val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
    val scale = 1f - 0.08f * offset.coerceIn(0f, 1f)
    scaleX = scale
    scaleY = scale
}

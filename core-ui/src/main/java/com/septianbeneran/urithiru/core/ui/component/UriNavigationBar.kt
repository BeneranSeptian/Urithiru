package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

enum class UriNavigationLabelMode { Always, SelectedOnly, Never }

@Immutable
data class UriNavigationBarItem(
    val label: String,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int = icon
)

/**
 * How [UriNavigationBar] looks and moves. A floating bar is a rounded, elevated pill held away from the
 * screen edges by the margins; a docked bar (`isFloating = false`) spans the full width and ignores
 * [shape], the margins and [shadowElevation].
 */
@Immutable
data class UriNavigationBarStyle(
    val isFloating: Boolean = true,
    val shape: Shape = RoundedCornerShape(28.dp),
    val horizontalMargin: Dp = 16.dp,
    val bottomMargin: Dp = 12.dp,
    val shadowElevation: Dp = 8.dp,
    val height: Dp = 64.dp,
    val showIndicator: Boolean = true,
    val indicatorShape: Shape = RoundedCornerShape(22.dp),
    val indicatorPadding: Dp = 6.dp,
    val labelMode: UriNavigationLabelMode = UriNavigationLabelMode.SelectedOnly,
    val selectedIconScale: Float = 1.1f,
    val springDampingRatio: Float = Spring.DampingRatioLowBouncy,
    val springStiffness: Float = Spring.StiffnessMediumLow,
    val hapticFeedback: Boolean = true
)

@Immutable
data class UriNavigationBarColors(
    val container: Color,
    val indicator: Color,
    val selectedContent: Color,
    val unselectedContent: Color
)

object UriNavigationBarDefaults {
    val Floating = UriNavigationBarStyle()

    val Docked = UriNavigationBarStyle(
        isFloating = false,
        labelMode = UriNavigationLabelMode.Always
    )

    @Composable
    fun colors(
        container: Color = MaterialTheme.colorScheme.surfaceContainer,
        indicator: Color = MaterialTheme.colorScheme.primaryContainer,
        selectedContent: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedContent: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ) = UriNavigationBarColors(
        container = container,
        indicator = indicator,
        selectedContent = selectedContent,
        unselectedContent = unselectedContent
    )
}

/**
 * A bottom navigation bar with a sliding selection indicator and springy icons. It pads itself for the
 * system navigation bar. Pass `selectedIndex = -1` when no item is selected.
 */
@Composable
fun UriNavigationBar(
    modifier: Modifier = Modifier,
    items: List<UriNavigationBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    style: UriNavigationBarStyle = UriNavigationBarDefaults.Floating,
    colors: UriNavigationBarColors = UriNavigationBarDefaults.colors()
) {
    val outerModifier = if (style.isFloating) {
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = style.horizontalMargin, end = style.horizontalMargin, bottom = style.bottomMargin)
    } else {
        modifier.fillMaxWidth()
    }

    Surface(
        modifier = outerModifier,
        shape = if (style.isFloating) style.shape else RectangleShape,
        color = colors.container,
        shadowElevation = if (style.isFloating) style.shadowElevation else 0.dp
    ) {
        BarItems(
            modifier = if (style.isFloating) Modifier else Modifier.navigationBarsPadding(),
            items = items,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            style = style,
            colors = colors
        )
    }
}

@Composable
private fun BarItems(
    modifier: Modifier,
    items: List<UriNavigationBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    style: UriNavigationBarStyle,
    colors: UriNavigationBarColors
) {
    val haptic = LocalHapticFeedback.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(style.height)
    ) {
        val itemWidth = maxWidth / items.size.coerceAtLeast(1)

        if (style.showIndicator && selectedIndex in items.indices) {
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(dampingRatio = style.springDampingRatio, stiffness = style.springStiffness),
                label = "indicatorOffset"
            )
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(style.indicatorPadding)
                    .background(colors.indicator, style.indicatorShape)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .selectableGroup()
        ) {
            items.forEachIndexed { index, item ->
                BarItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    item = item,
                    isSelected = index == selectedIndex,
                    style = style,
                    colors = colors,
                    onClick = {
                        if (index != selectedIndex && style.hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        }
                        onSelect(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun BarItem(
    modifier: Modifier,
    item: UriNavigationBarItem,
    isSelected: Boolean,
    style: UriNavigationBarStyle,
    colors: UriNavigationBarColors,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) colors.selectedContent else colors.unselectedContent,
        label = "contentColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) style.selectedIconScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "iconScale"
    )
    val isLabelVisible = when (style.labelMode) {
        UriNavigationLabelMode.Always -> true
        UriNavigationLabelMode.SelectedOnly -> isSelected
        UriNavigationLabelMode.Never -> false
    }

    Column(
        modifier = modifier
            .padding(style.indicatorPadding)
            .clip(style.indicatorShape)
            .selectable(selected = isSelected, onClick = onClick, role = Role.Tab),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(targetState = isSelected, label = "icon") { selected ->
            Icon(
                painter = painterResource(if (selected) item.selectedIcon else item.icon),
                contentDescription = if (isLabelVisible) null else item.label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale)
            )
        }
        AnimatedVisibility(
            visible = isLabelVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = item.label,
                style = UrithiruTheme.typography.actionS,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val PreviewItems = listOf(
    UriNavigationBarItem("Catalog", R.drawable.widgets, R.drawable.widgets_filled),
    UriNavigationBarItem("Shop", R.drawable.shopping_bag, R.drawable.shopping_bag_filled),
    UriNavigationBarItem("Explore", R.drawable.explore, R.drawable.explore_filled),
    UriNavigationBarItem("Profile", R.drawable.person, R.drawable.person_filled)
)

@Preview(showBackground = true)
@Composable
private fun UriNavigationBarFloatingPrev() {
    UrithiruTheme {
        var selected by remember { mutableIntStateOf(0) }
        UriNavigationBar(items = PreviewItems, selectedIndex = selected, onSelect = { selected = it })
    }
}

@Preview(showBackground = true)
@Composable
private fun UriNavigationBarDockedPrev() {
    UrithiruTheme {
        var selected by remember { mutableIntStateOf(2) }
        UriNavigationBar(
            items = PreviewItems,
            selectedIndex = selected,
            onSelect = { selected = it },
            style = UriNavigationBarDefaults.Docked
        )
    }
}

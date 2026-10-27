package com.septianbeneran.urithiru.feature.onboarding.screen.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.component.UriBadge
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.component.UriRadioButton
import com.septianbeneran.urithiru.core.ui.component.UriSkeletonListItem
import com.septianbeneran.urithiru.core.ui.component.UriToast
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import kotlinx.coroutines.delay

@Composable
fun ComponentsDemo(modifier: Modifier = Modifier) {
    var visibleCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (visibleCount < DEMO_ITEM_COUNT) {
            delay(300)
            visibleCount++
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DemoItem(visible = visibleCount > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UriButton(text = "Primary", onClick = {})
                UriButton(type = Secondary, text = "Secondary", onClick = {})
            }
        }
        DemoItem(visible = visibleCount > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UriBadge(type = UriBadgeProperties.Type.Number("3"))
                UriBadge(type = UriBadgeProperties.Type.Icon)
                UriBadge(type = UriBadgeProperties.Type.Dot)
                UriRadioButton(selected = true)
            }
        }
        DemoItem(visible = visibleCount > 2) {
            UriSkeletonListItem()
        }
        DemoItem(visible = visibleCount > 3) {
            UriToast(
                titleText = "Saved",
                subTitleText = "Toasts, banners and dialogs are included too.",
                onClose = {}
            )
        }
    }
}

@Composable
private fun DemoItem(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        content()
    }
}

private const val DEMO_ITEM_COUNT = 4

@Preview(showBackground = true)
@Composable
private fun ComponentsDemoPrev() {
    UrithiruTheme {
        ComponentsDemo()
    }
}

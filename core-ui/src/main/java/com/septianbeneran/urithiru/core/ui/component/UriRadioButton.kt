package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties.Size
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties.Size.Large
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties.Size.Medium
import com.septianbeneran.urithiru.core.ui.component.UriRadioButtonProperties.Size.Small
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light100

@Composable
fun UriRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    size: Size = Medium,
    onSelected: (Boolean) -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(size.circleSize)
            .border(
                width = 1.5f.dp,
                color = if (selected) Color.Transparent else Neutral.Light.Light500,
                shape = CircleShape
            )
            .background(if (selected) Highlight.Highlight500 else Light100)
            .clickable(enabled = enabled, onClick = { onSelected(!selected) }),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size.innerCircleSize)
                .clip(CircleShape)
                .background(Light100)
        )
    }
}

object UriRadioButtonProperties {
    sealed class Size(
        val circleSize: Dp,
        val innerCircleSize: Dp
    ) {
        object Small : Size(
            circleSize = 16.dp,
            innerCircleSize = 6.dp
        )

        object Medium : Size(
            circleSize = 24.dp,
            innerCircleSize = 10.dp
        )

        object Large : Size(
            circleSize = 32.dp,
            innerCircleSize = 12.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriRadioButtonPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UriRadioButton(
                size = Small,
                selected = true
            )

            UriRadioButton(
                size = Medium,
                selected = true
            )

            UriRadioButton(
                size = Large,
                selected = true
            )

            UriRadioButton(
                size = Large,
                selected = false
            )

            UriRadioButton(
                size = Medium,
                selected = false
            )

            UriRadioButton(
                size = Small,
                selected = false
            )
        }
    }
}
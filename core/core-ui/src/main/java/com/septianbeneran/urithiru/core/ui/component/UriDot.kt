package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral

@Composable
fun UriDot(
    modifier: Modifier = Modifier,
    isFilled: Boolean = false,
    filledColor: Color = Highlight.Highlight500,
    unfilledColor: Color = Neutral.Dark.Dark100
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(8.dp)
            .background(if (isFilled) filledColor else unfilledColor)
    )
}

@Preview(showBackground = true)
@Composable
private fun UriDotPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriDot(isFilled = false)
            UriDot(isFilled = true)
        }
    }
}
package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light300

@Composable
fun UriProgressBar(
    modifier: Modifier = Modifier,
    progress: Float
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .background(Light300),
    ) {
        Box(
            modifier = Modifier
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth(safeProgress)
                .background(Highlight.Highlight500)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriProgressBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriProgressBar(progress = 1f)

            UriProgressBar(progress = 0.5f)

            UriProgressBar(progress = 0f)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val progresses = listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f, 1f)
                progresses.forEach { prog ->
                    UriProgressBar(
                        progress = prog,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
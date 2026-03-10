package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties.Size
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties.Size.Large
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties.Size.Medium
import com.septianbeneran.urithiru.core.ui.component.UriCheckBoxProperties.Size.Small
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral

@Composable
fun UriCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    size: Size = Medium,
    onChecked: (Boolean) -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(size.boxSize)
            .clip(RoundedCornerShape(size.roundedCorner))
            .border(
                width = 1.dp,
                color = if (checked) Color.Transparent else Neutral.Light.Light500,
                shape = RoundedCornerShape(size.roundedCorner)
            )
            .background(if (checked) Highlight.Highlight500 else Color.Transparent)
            .clickable(enabled = true, onClick = { onChecked(!checked) }),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.check_rounded),
                contentDescription = null,
                tint = Neutral.Light.Light100,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

object UriCheckBoxProperties {
    sealed class Size(
        val boxSize: Dp,
        val roundedCorner: Dp
    ) {
        object Small : Size(
            boxSize = 16.dp,
            roundedCorner = 4.dp
        )

        object Medium : Size(
            boxSize = 24.dp,
            roundedCorner = 6.dp
        )

        object Large : Size(
            boxSize = 32.dp,
            roundedCorner = 8.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriCheckBoxPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                UriCheckBox(
                    checked = true,
                    size = Small
                )
                UriCheckBox(
                    checked = true,
                    size = Medium
                )
                UriCheckBox(
                    checked = true,
                    size = Large
                )
            }

            Spacer(Modifier.padding(vertical = 16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                UriCheckBox(
                    checked = false,
                    size = Small
                )
                UriCheckBox(
                    checked = false,
                    size = Medium
                )
                UriCheckBox(
                    checked = false,
                    size = Large
                )
            }
        }
    }
}
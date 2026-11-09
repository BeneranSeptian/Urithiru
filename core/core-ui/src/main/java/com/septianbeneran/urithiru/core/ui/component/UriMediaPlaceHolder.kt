package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.Highlight

@Composable
fun UriMediaPlaceHolder(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.image,
    backgroundColor: Color = Highlight.Highlight100,
    iconTint: Color = Highlight.Highlight200,
    iconSize: Dp = 32.dp
) {
    Box(
        modifier = modifier.size(120.dp).background(backgroundColor),
        contentAlignment = Center
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            colorFilter = ColorFilter.tint(iconTint),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriMediaPlaceHolderPreview() {
    Box(modifier= Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriMediaPlaceHolder(
                icon = R.drawable.image
            )

            UriMediaPlaceHolder(
                icon = R.drawable.play,
                iconSize = 40.dp
            )
        }

    }
}
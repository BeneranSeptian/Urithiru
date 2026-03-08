package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriAvatarProperties.L
import com.septianbeneran.urithiru.core.ui.component.UriAvatarProperties.M
import com.septianbeneran.urithiru.core.ui.component.UriAvatarProperties.S
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight100
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight200

@Composable
fun UriAvatar(
    size: UriAvatarProperties.Size = S,
    iconTint: Color = Highlight200,
    backgroundColor: Color = Highlight100
) {
    val iconModifier = when (size) {
        S -> Modifier
            .size(
                width = 24.dp,
                height = 42.dp
            )

        M -> Modifier
            .size(width = 40.dp, height = 65.dp)
            .offset(y = 10.dp)

        L -> Modifier
            .size(width = 60.dp, height = 98.dp)
            .offset(y = 12.dp)
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .size(size.width, size.height)
            .clip(RoundedCornerShape(size.cornerRadius))
            .background(backgroundColor),
        contentAlignment = Center
    ) {
        Box(
            modifier = Modifier
                .then(iconModifier)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(iconTint),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

object UriAvatarProperties {
    data class Size(
        val width: Dp,
        val height: Dp,
        val cornerRadius: Dp,
        val iconSize: Dp
    )

    val S = Size(
        width = 40.dp,
        height = 42.dp,
        cornerRadius = 16.dp,
        iconSize = 24.dp
    )

    val M = Size(
        width = 56.dp,
        height = 56.dp,
        cornerRadius = 20.dp,
        iconSize = 40.dp
    )

    val L = Size(
        width = 80.dp,
        height = 80.dp,
        cornerRadius = 32.dp,
        iconSize = 60.dp
    )
}


@Preview(showBackground = true)
@Composable
private fun UriAvatarPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UriAvatar(
                size = S
            )

            UriAvatar(
                size = M
            )

            UriAvatar(
                size = L
            )
        }
    }
}
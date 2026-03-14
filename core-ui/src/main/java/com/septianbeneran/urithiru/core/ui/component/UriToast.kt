package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType.Error
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType.Info
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType.Success
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType.Warning
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Dark.Dark200
import com.septianbeneran.urithiru.core.ui.theme.Support
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun UriToast(
    modifier: Modifier = Modifier,
    type: UriToastType = Success,
    titleText: String,
    subTitleText: String,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(type.backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(type.icon),
                contentDescription = null,
                tint = type.iconColor,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    style = UrithiruTheme.typography.h5,
                    maxLines = 1,
                    overflow = Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subTitleText,
                    style = UrithiruTheme.typography.bodyS,
                    maxLines = 2,
                    overflow = Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = null,
                tint = Dark200,
                modifier = Modifier.clickable(enabled = true, onClick = onClose)
            )
        }
    }
}

object UriToastProperties {
    enum class UriToastType(
        val backgroundColor: Color,
        val iconColor: Color,
        @param:DrawableRes val icon: Int
    ) {
        Success(
            backgroundColor = Support.Success.Success100,
            iconColor = Support.Success.Success300,
            icon = R.drawable.success
        ),
        Info(
            backgroundColor = Highlight.Highlight100,
            iconColor = Highlight.Highlight500,
            icon = R.drawable.info
        ),
        Warning(
            backgroundColor = Support.Warning.Warning100,
            iconColor = Support.Warning.Warning300,
            icon = R.drawable.error
        ),
        Error(
            backgroundColor = Support.Error.Error100,
            iconColor = Support.Error.Error300,
            icon = R.drawable.error
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriToastPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriToast(
                titleText = "Success",
                subTitleText = "Subtitle lorem lorem lorem lorem lorem lorem lorem lorem lorem.",
                type = Success,
                onClose = {}
            )

            UriToast(
                titleText = "Info",
                subTitleText = "Subtitle lorem lorem lorem lorem lorem lorem lorem lorem lorem.",
                type = Info,
                onClose = {}
            )

            UriToast(
                titleText = "Warning",
                subTitleText = "Subtitle lorem lorem lorem lorem lorem lorem lorem lorem lorem.",
                type = Warning,
                onClose = {}
            )

            UriToast(
                titleText = "Error",
                subTitleText = "Subtitle lorem lorem lorem lorem lorem lorem lorem lorem lorem.",
                type = Error,
                onClose = {}
            )
        }
    }
}
package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun UriBanner(
    modifier: Modifier = Modifier,
    titleText: String,
    subTitleText: String,
    buttonText: String,
    onClick: () -> Unit = {},
    onClickButton: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Highlight.Highlight100)
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onClick)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 16.dp)
            ) {
                Text(
                    text = titleText,
                    style = UrithiruTheme.typography.h4,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subTitleText,
                    style = UrithiruTheme.typography.bodyS,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))
                UriButton(
                    text = buttonText,
                    onClick = onClickButton
                )
            }
            UriMediaPlaceHolder(modifier = Modifier.weight(0.5f).fillMaxHeight())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriBannerPreview() {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        UriBanner(
            titleText = "Title",
            subTitleText = "Description. Lorem ipsum dolor sit amet consectetur adipiscing elit, sed do.",
            buttonText = "Button"
        )
    }
}
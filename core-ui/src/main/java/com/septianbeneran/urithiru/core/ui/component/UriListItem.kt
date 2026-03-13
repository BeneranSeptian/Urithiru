package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties.Type.Number
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun UriListItem(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = if (description == null) Alignment.CenterVertically else Alignment.Top
        ) {
            leadingContent?.invoke()
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = UrithiruTheme.typography.bodyM,
                    color = Neutral.Dark.Dark500,
                    maxLines = 1,
                    overflow = Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    description?.let {
                        Text(
                            text = description,
                            style = UrithiruTheme.typography.bodyS,
                            color = Neutral.Dark.Dark200,
                            maxLines = 3,
                            overflow = Ellipsis,
                            modifier = Modifier.weight(1f).padding(top = 4.dp)
                        )
                        trailingContent?.invoke()
                    }
                }
            }

            if (description.isNullOrBlank()) {
                trailingContent?.invoke()
            }
        }
    }
}

object UriListItemProperties {

}

@Preview(showBackground = true)
@Composable
private fun UriStepperPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriListItem(
                title = "Title",
                description = "Description. Lorem ipsum dolor sit amet consectetur adipiscing elit, sed do. Description. Lorem ipsum dolor sit amet consectetur adipiscing elit, sed do",
                leadingContent = {
                    UriAvatar()
                },
                trailingContent = {
                    UriBadge(
                        type = Number("1")
                    )
                }
            )

            UriListItem(
                title = "Title Beneran Panjang buat ngetes",
                trailingContent = {
                    UriBadge(
                        type = Number("1")
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Highlight.Highlight100)
            )
        }
    }

}
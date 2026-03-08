package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Primary
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight500
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun UriButton(
    modifier: Modifier = Modifier,
    type: Type = Primary,
    text: String,
    @DrawableRes leadingIcon: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = type.borderColor?.let { BorderStroke(1.dp, it) },
        colors = ButtonDefaults.buttonColors(
            containerColor = type.containerColor,
            contentColor = type.contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.5f.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = Medium,
                maxLines = 1
            )
            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null
                )
            }
        }
    }
}

object UriButtonProperties {
    sealed class Type(
        val containerColor: Color,
        val contentColor: Color,
        val borderColor: Color? = null
    ) {
        object Primary : Type(
            containerColor = Highlight500,
            contentColor = Neutral.Light.Light100
        )

        object Secondary : Type(
            containerColor = Transparent,
            contentColor = Highlight500,
            borderColor = Highlight500
        )

        object Tertiary : Type(
            containerColor = Transparent,
            contentColor = Highlight500
        )

        class Custom(
            containerColor: Color,
            contentColor: Color,
            borderColor: Color? = null
        ) : Type(containerColor, contentColor, borderColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun UriButtonPrev() {
    UrithiruTheme() {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    UriButton(
                        type = Primary,
                        text = "Primary"
                    )

                    UriButton(
                        type = Secondary,
                        text = "Secondary"
                    )

                    UriButton(
                        type = Type.Tertiary,
                        text = "Tertiary"
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    UriButton(
                        type = Primary,
                        text = "Primary",
                        leadingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play,
                        trailingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play
                    )

                    UriButton(
                        type = Secondary,
                        text = "Secondary",
                        leadingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play,
                        trailingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play
                    )

                    UriButton(
                        type = Type.Tertiary,
                        text = "Tertiary",
                        leadingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play,
                        trailingIcon = com.septianbeneran.urithiru.core.ui.R.drawable.play
                    )
                }
            }
        }
    }
}
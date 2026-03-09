package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties.Type.Dot
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties.Type.Icon
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties.Type.Number
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTypography

@Composable
fun UriBadge(
    modifier: Modifier = Modifier,
    type: UriBadgeProperties.Type = Icon
) {
    Box(
        modifier = modifier
            .size(if (type is Number) 24.dp else 16.dp)
            .clip(CircleShape)
            .background(Highlight.Highlight500),
        contentAlignment = Alignment.Center
    ) {
        when(type) {
            Dot -> Unit
            Icon -> Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                painter = painterResource(R.drawable.check_rounded),
                contentDescription = null,
                tint = Neutral.Light.Light100,
            )
            is Number -> Text(
                text = type.numberText,
                color = Neutral.Light.Light100,
                style = UrithiruTypography.labelSmall
            )
        }
    }
}

object UriBadgeProperties {
    sealed class Type {
        data class Number(val numberText: String) : Type()
        object Icon : Type()
        object Dot : Type()
    }
}

@Preview(showBackground = true)
@Composable
private fun UriBadgePreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UriBadge(
                type = Dot
            )
            UriBadge(
                type = Icon
            )
            UriBadge(
                type = Number("1")
            )
        }
    }
}
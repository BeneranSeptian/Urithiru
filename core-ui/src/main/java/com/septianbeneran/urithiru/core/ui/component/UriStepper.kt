package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriStepperProperties.Type.Done
import com.septianbeneran.urithiru.core.ui.component.UriStepperProperties.Type.Filled
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun UriStepper(
    modifier: Modifier = Modifier,
    type: UriStepperProperties.Type = Filled,
    stepText: String? = null,
    titleText: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .size(24.dp)
                .background(type.stepContainerColor),
            contentAlignment = Alignment.Center
        ) {
            if(stepText.isNullOrBlank().not() && type != Done) {
                Text(
                    text = stepText,
                    style = UrithiruTheme.typography.actionS,
                    color = type.stepTextColor
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp)
                        .background(
                            color = type.stepContainerColor
                        )
                        .padding(2.dp),
                    painter = painterResource(R.drawable.check_rounded),
                    contentDescription = null,
                    tint = type.stepTextColor,
                )
            }
        }

        titleText?.let {
            Text(
                text = it,
                style = UrithiruTheme.typography.h5,
                color = type.titleTextColor
            )
        }
    }
}

object UriStepperProperties {
    sealed class Type(
        val titleTextColor: Color,
        val stepContainerColor: Color,
        val stepTextColor: Color
    ) {
        object Filled: Type(
            titleTextColor = Neutral.Dark.Dark500,
            stepContainerColor = Highlight.Highlight500,
            stepTextColor = Neutral.Light.Light100
        )

        object Unfilled: Type(
            titleTextColor = Neutral.Dark.Dark100,
            stepContainerColor = Neutral.Light.Light300,
            stepTextColor = Neutral.Dark.Dark100
        )

        object Done: Type(
            titleTextColor = Neutral.Dark.Dark100,
            stepContainerColor = Highlight.Highlight200,
            stepTextColor = Highlight.Highlight500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UriStepperPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriStepper(
                stepText = "1",
                titleText = "Step"
            )

            UriStepper(
                type = UriStepperProperties.Type.Unfilled,
                stepText = "1",
                titleText = "Step"
            )

            UriStepper(
                type = Done,
                stepText = "1",
                titleText = "Step"
            )
        }
    }
}
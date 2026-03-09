package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties.Type
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties.Type.Alert
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties.Type.Confirmation
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UriDialog(
    modifier: Modifier = Modifier,
    textTitle: String,
    textDescription: String,
    type: Type = Alert(
        primaryButtonText = "",
        onPrimaryButtonClick = {}
    ),
    onDismissRequest: () -> Unit,
    isCancelable: Boolean = false
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = isCancelable,
            dismissOnClickOutside = isCancelable
        )
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Neutral.Light.Light100)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                TextSection(
                    textTitle = textTitle,
                    textDescription = textDescription
                )

                ButtonSection(type = type)
            }
        }
    }
}

@Composable
private fun TextSection(
    textTitle: String,
    textDescription: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = textTitle,
            style = UrithiruTypography.headlineSmall,
            maxLines = 2
        )

        Text(
            text = textDescription,
            style = UrithiruTypography.bodySmall,
            fontSize = 12.sp,
            maxLines = 3,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    type: Type
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (
            type is Confirmation &&
            type.secondaryButtonText != null &&
            type.onSecondaryButtonClick != null
        ) {
            UriButton(
                text = type.secondaryButtonText,
                modifier = modifier.weight(1f),
                type = UriButtonProperties.Type.Secondary,
                onClick = type.onSecondaryButtonClick
            )
        }
        UriButton(
            text = type.primaryButtonText,
            modifier = modifier.weight(1f),
            type = UriButtonProperties.Type.Primary,
            onClick = type.onPrimaryButtonClick
        )
    }
}

object UriDialogProperties {
    sealed class Type(
        val primaryButtonText: String,
        val secondaryButtonText: String? = null,
        val onPrimaryButtonClick: () -> Unit,
        val onSecondaryButtonClick: (() -> Unit)? = null,
    ) {
        class Alert(
            primaryButtonText: String,
            onPrimaryButtonClick: () -> Unit,
        ) : Type(
            primaryButtonText = primaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick
        )

        class Confirmation(
            primaryButtonText: String,
            secondaryButtonText: String,
            onPrimaryButtonClick: () -> Unit,
            onSecondaryButtonClick: () -> Unit,
        ) : Type(
            primaryButtonText = primaryButtonText,
            secondaryButtonText = secondaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            onSecondaryButtonClick = onSecondaryButtonClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UriDialogPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        UriDialog(
            textTitle = "Title",
            textDescription = "Description on dialog pop up to show how it works",
            type = Alert(
                primaryButtonText = "Primary Button",
                onPrimaryButtonClick = {}
            ),
            onDismissRequest = {}
        )

        UriDialog(
            textTitle = "Title",
            textDescription = "Description on dialog pop up to show how it works",
            type = Alert(
                primaryButtonText = "Primary Button",
                onPrimaryButtonClick = {}
            ),
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UriDialogTwoButtonPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        UriDialog(
            textTitle = "Title",
            textDescription = "Description on dialog pop up to show how it works",
            type = Confirmation(
                primaryButtonText = "Primary Button",
                onPrimaryButtonClick = {},
                secondaryButtonText = "Secondary Button",
                onSecondaryButtonClick = {}
            ),
            onDismissRequest = {}
        )
    }
}
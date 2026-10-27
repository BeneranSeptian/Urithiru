package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriBanner
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Tertiary
import com.septianbeneran.urithiru.core.ui.component.UriDialog
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties
import com.septianbeneran.urithiru.core.ui.component.UriToast
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType

@Composable
fun ButtonDemo() {
    DemoSection("Types") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = "Primary", onClick = {})
            UriButton(type = Secondary, text = "Secondary", onClick = {})
            UriButton(type = Tertiary, text = "Tertiary", onClick = {})
        }
    }
    DemoSection("With icons") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = "Play", leadingIcon = R.drawable.play, onClick = {})
            UriButton(type = Secondary, text = "Next", trailingIcon = R.drawable.chevron_right, onClick = {})
        }
    }
    DemoSection("Full width") {
        UriButton(modifier = Modifier.fillMaxWidth(), text = "Continue", onClick = {})
    }
}

@Composable
fun DialogDemo() {
    var openDialog by rememberSaveable { mutableStateOf<String?>(null) }

    DemoSection("Try it") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = "Alert", onClick = { openDialog = "alert" })
            UriButton(type = Secondary, text = "Confirmation", onClick = { openDialog = "confirmation" })
        }
    }

    when (openDialog) {
        "alert" -> UriDialog(
            textTitle = "Saved",
            textDescription = "Your changes have been saved.",
            type = UriDialogProperties.Type.Alert(
                primaryButtonText = "OK",
                onPrimaryButtonClick = { openDialog = null }
            ),
            onDismissRequest = { openDialog = null },
            isCancelable = true
        )
        "confirmation" -> UriDialog(
            textTitle = "Remove favorite?",
            textDescription = "You can add it again at any time.",
            type = UriDialogProperties.Type.Confirmation(
                primaryButtonText = "Remove",
                secondaryButtonText = "Cancel",
                onPrimaryButtonClick = { openDialog = null },
                onSecondaryButtonClick = { openDialog = null }
            ),
            onDismissRequest = { openDialog = null },
            isCancelable = true
        )
    }
}

@Composable
fun ToastDemo() {
    var isDismissibleVisible by rememberSaveable { mutableStateOf(true) }

    DemoSection("Types") {
        UriToast(type = UriToastType.Success, titleText = "Success", subTitleText = "Your order was placed.", onClose = {})
        UriToast(type = UriToastType.Info, titleText = "Info", subTitleText = "A new version is available.", onClose = {})
        UriToast(type = UriToastType.Warning, titleText = "Warning", subTitleText = "Your session expires soon.", onClose = {})
        UriToast(type = UriToastType.Error, titleText = "Error", subTitleText = "The payment failed.", onClose = {})
    }
    DemoSection("Dismissible") {
        if (isDismissibleVisible) {
            UriToast(
                type = UriToastType.Info,
                titleText = "Tap the X",
                subTitleText = "The toast closes itself.",
                onClose = { isDismissibleVisible = false }
            )
        } else {
            UriButton(type = Secondary, text = "Show again", onClick = { isDismissibleVisible = true })
        }
    }
}

@Composable
fun BannerDemo() {
    DemoSection("Default") {
        UriBanner(
            titleText = "Upgrade to Pro",
            subTitleText = "Unlock offline mode and favorites sync.",
            buttonText = "Learn more"
        )
    }
}

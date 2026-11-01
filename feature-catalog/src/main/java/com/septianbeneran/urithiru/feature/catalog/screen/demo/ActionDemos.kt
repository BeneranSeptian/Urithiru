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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.component.UriBanner
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Tertiary
import com.septianbeneran.urithiru.core.ui.component.UriDialog
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties
import com.septianbeneran.urithiru.core.ui.component.UriToast
import com.septianbeneran.urithiru.core.ui.component.UriToastProperties.UriToastType
import com.septianbeneran.urithiru.feature.catalog.R

@Composable
fun ButtonDemo() {
    DemoSection(stringResource(R.string.catalog_demo_types)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = stringResource(CoreUiR.string.common_primary), onClick = {})
            UriButton(type = Secondary, text = stringResource(CoreUiR.string.common_secondary), onClick = {})
            UriButton(type = Tertiary, text = stringResource(R.string.catalog_demo_tertiary), onClick = {})
        }
    }
    DemoSection(stringResource(R.string.catalog_demo_with_icons)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = stringResource(R.string.catalog_demo_play), leadingIcon = CoreUiR.drawable.play, onClick = {})
            UriButton(type = Secondary, text = stringResource(CoreUiR.string.common_next), trailingIcon = CoreUiR.drawable.chevron_right, onClick = {})
        }
    }
    DemoSection(stringResource(R.string.catalog_demo_full_width)) {
        UriButton(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.catalog_demo_continue), onClick = {})
    }
}

@Composable
fun DialogDemo() {
    var openDialog by rememberSaveable { mutableStateOf<String?>(null) }

    DemoSection(stringResource(R.string.catalog_demo_try_it)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(text = stringResource(R.string.catalog_demo_alert), onClick = { openDialog = "alert" })
            UriButton(type = Secondary, text = stringResource(R.string.catalog_demo_confirmation), onClick = { openDialog = "confirmation" })
        }
    }

    when (openDialog) {
        "alert" -> UriDialog(
            textTitle = stringResource(CoreUiR.string.common_saved),
            textDescription = stringResource(R.string.catalog_demo_saved_message),
            type = UriDialogProperties.Type.Alert(
                primaryButtonText = stringResource(R.string.catalog_demo_ok),
                onPrimaryButtonClick = { openDialog = null }
            ),
            onDismissRequest = { openDialog = null },
            isCancelable = true
        )
        "confirmation" -> UriDialog(
            textTitle = stringResource(R.string.catalog_demo_remove_favorite),
            textDescription = stringResource(R.string.catalog_demo_remove_favorite_message),
            type = UriDialogProperties.Type.Confirmation(
                primaryButtonText = stringResource(R.string.catalog_demo_remove),
                secondaryButtonText = stringResource(CoreUiR.string.common_cancel),
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

    DemoSection(stringResource(R.string.catalog_demo_types)) {
        UriToast(type = UriToastType.Success, titleText = stringResource(R.string.catalog_demo_success), subTitleText = stringResource(R.string.catalog_demo_success_message), onClose = {})
        UriToast(type = UriToastType.Info, titleText = stringResource(R.string.catalog_demo_info), subTitleText = stringResource(R.string.catalog_demo_info_message), onClose = {})
        UriToast(type = UriToastType.Warning, titleText = stringResource(R.string.catalog_demo_warning), subTitleText = stringResource(R.string.catalog_demo_warning_message), onClose = {})
        UriToast(type = UriToastType.Error, titleText = stringResource(R.string.catalog_demo_error), subTitleText = stringResource(R.string.catalog_demo_error_message), onClose = {})
    }
    DemoSection(stringResource(R.string.catalog_demo_dismissible)) {
        if (isDismissibleVisible) {
            UriToast(
                type = UriToastType.Info,
                titleText = stringResource(R.string.catalog_demo_tap_close),
                subTitleText = stringResource(R.string.catalog_demo_tap_close_message),
                onClose = { isDismissibleVisible = false }
            )
        } else {
            UriButton(type = Secondary, text = stringResource(R.string.catalog_demo_show_again), onClick = { isDismissibleVisible = true })
        }
    }
}

@Composable
fun BannerDemo() {
    DemoSection(stringResource(R.string.catalog_demo_default)) {
        UriBanner(
            titleText = stringResource(R.string.catalog_demo_banner_title),
            subTitleText = stringResource(R.string.catalog_demo_banner_message),
            buttonText = stringResource(R.string.catalog_demo_learn_more)
        )
    }
}

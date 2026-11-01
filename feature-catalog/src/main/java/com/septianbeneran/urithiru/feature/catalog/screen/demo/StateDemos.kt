package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.component.CentralLoadingDialog
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriListFooter
import com.septianbeneran.urithiru.core.ui.component.UriLoader
import com.septianbeneran.urithiru.core.ui.component.UriProgressBar
import com.septianbeneran.urithiru.core.ui.component.UriSkeleton
import com.septianbeneran.urithiru.core.ui.component.UriSkeletonListItem
import com.septianbeneran.urithiru.core.ui.localization.localizedString
import com.septianbeneran.urithiru.feature.catalog.R
import kotlinx.coroutines.delay

@Composable
fun LoaderDemo() {
    var isDialogShown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isDialogShown) {
        if (isDialogShown) {
            delay(2000)
            isDialogShown = false
        }
    }

    DemoSection(localizedString(R.string.catalog_demo_spinner)) {
        UriLoader()
    }
    DemoSection("CentralLoadingDialog") {
        UriButton(text = localizedString(R.string.catalog_demo_show_two_seconds), onClick = { isDialogShown = true })
    }
    if (isDialogShown) CentralLoadingDialog()
}

@Composable
fun ProgressBarDemo() {
    var progress by rememberSaveable { mutableFloatStateOf(0.4f) }

    DemoSection(localizedString(R.string.catalog_demo_try_it)) {
        UriProgressBar(modifier = Modifier.fillMaxWidth(), progress = progress)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(type = Secondary, text = "-10%", onClick = { progress = (progress - 0.1f).coerceAtLeast(0f) })
            UriButton(type = Secondary, text = "+10%", onClick = { progress = (progress + 0.1f).coerceAtMost(1f) })
        }
    }
}

@Composable
fun SkeletonDemo() {
    DemoSection(localizedString(R.string.catalog_demo_blocks)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            UriSkeleton(modifier = Modifier.size(64.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UriSkeleton(modifier = Modifier.fillMaxWidth().height(16.dp))
                UriSkeleton(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp))
            }
        }
    }
    DemoSection(localizedString(R.string.catalog_demo_list_rows)) {
        repeat(3) { UriSkeletonListItem() }
    }
}

@Composable
fun ErrorStateDemo() {
    DemoSection(localizedString(R.string.catalog_demo_retryable)) {
        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            UriErrorState(error = AppError.NoInternet, onRetry = {})
        }
    }
    DemoSection(localizedString(R.string.catalog_demo_not_retryable)) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            UriErrorState(error = AppError.NotFound, onRetry = {})
        }
    }
}

@Composable
fun EmptyStateDemo() {
    DemoSection(localizedString(R.string.catalog_demo_default)) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            UriEmptyState(title = localizedString(R.string.catalog_demo_no_weapons), message = localizedString(CoreUiR.string.common_try_different_name))
        }
    }
}

@Composable
fun ListFooterDemo() {
    DemoSection(localizedString(R.string.catalog_demo_loading_next_page)) {
        UriListFooter(isLoading = true, error = null, onRetry = {})
    }
    DemoSection(localizedString(R.string.catalog_demo_next_page_failed)) {
        UriListFooter(isLoading = false, error = AppError.Timeout, onRetry = {})
    }
}

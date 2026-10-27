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

    DemoSection("Spinner") {
        UriLoader()
    }
    DemoSection("CentralLoadingDialog") {
        UriButton(text = "Show for 2 seconds", onClick = { isDialogShown = true })
    }
    if (isDialogShown) CentralLoadingDialog()
}

@Composable
fun ProgressBarDemo() {
    var progress by rememberSaveable { mutableFloatStateOf(0.4f) }

    DemoSection("Try it") {
        UriProgressBar(modifier = Modifier.fillMaxWidth(), progress = progress)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            UriButton(type = Secondary, text = "-10%", onClick = { progress = (progress - 0.1f).coerceAtLeast(0f) })
            UriButton(type = Secondary, text = "+10%", onClick = { progress = (progress + 0.1f).coerceAtMost(1f) })
        }
    }
}

@Composable
fun SkeletonDemo() {
    DemoSection("Blocks") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            UriSkeleton(modifier = Modifier.size(64.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UriSkeleton(modifier = Modifier.fillMaxWidth().height(16.dp))
                UriSkeleton(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp))
            }
        }
    }
    DemoSection("List rows") {
        repeat(3) { UriSkeletonListItem() }
    }
}

@Composable
fun ErrorStateDemo() {
    DemoSection("Retryable, e.g. no internet") {
        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            UriErrorState(error = AppError.NoInternet, onRetry = {})
        }
    }
    DemoSection("Not retryable, e.g. not found") {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            UriErrorState(error = AppError.NotFound, onRetry = {})
        }
    }
}

@Composable
fun EmptyStateDemo() {
    DemoSection("Default") {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            UriEmptyState(title = "No weapons found", message = "Try a different name.")
        }
    }
}

@Composable
fun ListFooterDemo() {
    DemoSection("Loading the next page") {
        UriListFooter(isLoading = true, error = null, onRetry = {})
    }
    DemoSection("Next page failed") {
        UriListFooter(isLoading = false, error = AppError.Timeout, onRetry = {})
    }
}

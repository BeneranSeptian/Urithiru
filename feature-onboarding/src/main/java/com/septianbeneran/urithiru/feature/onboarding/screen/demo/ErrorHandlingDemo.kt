package com.septianbeneran.urithiru.feature.onboarding.screen.demo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.component.UriSkeletonListItem
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.onboarding.screen.stateaction.DemoRequestState

@Composable
fun ErrorHandlingDemo(
    modifier: Modifier = Modifier,
    isOffline: Boolean,
    requestState: DemoRequestState,
    onOfflineChange: (Boolean) -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Go offline",
                    style = UrithiruTheme.typography.h4,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(checked = isOffline, onCheckedChange = onOfflineChange)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                when (requestState) {
                    DemoRequestState.Loading -> Column {
                        repeat(3) { UriSkeletonListItem() }
                    }
                    DemoRequestState.Error -> UriErrorState(
                        error = AppError.NoInternet,
                        onRetry = onRetry
                    )
                    DemoRequestState.Success -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        UriListItem(title = "Hand Axe", description = "Loaded from the API")
                        UriListItem(title = "Battle Axe", description = "Loaded from the API")
                        UriListItem(title = "Iron Cleaver", description = "Loaded from the API")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorHandlingDemoPrev() {
    UrithiruTheme {
        ErrorHandlingDemo(
            isOffline = true,
            requestState = DemoRequestState.Error,
            onOfflineChange = {},
            onRetry = {}
        )
    }
}

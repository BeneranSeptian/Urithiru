package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Secondary
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.toMessage

/**
 * Last item of a paginated list: a loader while the next page loads, or a message with Retry if it failed.
 * Shows nothing when neither applies.
 */
@Composable
fun UriListFooter(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    error: AppError?,
    onRetry: () -> Unit
) {
    when {
        isLoading -> Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UriLoader()
        }

        error != null -> Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.list_load_more_failed),
                    style = UrithiruTheme.typography.h5,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(error.toMessage()),
                    style = UrithiruTheme.typography.bodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
            UriButton(
                type = Secondary,
                text = stringResource(R.string.error_retry),
                onClick = onRetry
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriListFooterLoadingPrev() {
    UrithiruTheme {
        UriListFooter(isLoading = true, error = null, onRetry = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun UriListFooterErrorPrev() {
    UrithiruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            UriListFooter(isLoading = false, error = AppError.NoInternet, onRetry = {})
        }
    }
}

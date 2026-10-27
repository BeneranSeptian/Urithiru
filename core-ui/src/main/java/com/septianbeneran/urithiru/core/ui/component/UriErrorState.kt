package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.toMessage

/**
 * Full-screen failure: icon, title, a message for [error], and a retry button.
 * By default the button only shows when the error is worth retrying (see [AppError.isRetryable]).
 * Set [showRetry] to true where there's no other way forward, e.g. the splash screen.
 */
@Composable
fun UriErrorState(
    modifier: Modifier = Modifier,
    error: AppError,
    title: String = stringResource(R.string.error_title),
    @DrawableRes icon: Int = R.drawable.error,
    showRetry: Boolean = error.isRetryable,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = title,
            style = UrithiruTheme.typography.h4,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(error.toMessage()),
            style = UrithiruTheme.typography.bodyM,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null && showRetry) {
            UriButton(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.error_retry),
                onClick = onRetry
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriErrorStateRetryablePrev() {
    UrithiruTheme {
        UriErrorState(error = AppError.NoInternet, onRetry = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun UriErrorStateNotRetryablePrev() {
    UrithiruTheme {
        UriErrorState(error = AppError.NotFound, onRetry = {})
    }
}

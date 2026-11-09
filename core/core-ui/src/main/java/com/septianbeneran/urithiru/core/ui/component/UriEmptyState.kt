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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

/** Shown when a request succeeded but returned nothing, e.g. a search with no results. */
@Composable
fun UriEmptyState(
    modifier: Modifier = Modifier,
    title: String,
    message: String? = null,
    @DrawableRes icon: Int? = R.drawable.image,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = it),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = title,
            style = UrithiruTheme.typography.h4,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        message?.let {
            Text(
                text = it,
                style = UrithiruTheme.typography.bodyM,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        action?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun UriEmptyStatePrev() {
    UrithiruTheme {
        UriEmptyState(
            title = "No weapons found",
            message = "Try a different name."
        )
    }
}

package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.shimmerEffect

/** A shimmering placeholder block. Size it with [modifier]. */
@Composable
fun UriSkeleton(modifier: Modifier = Modifier) {
    Box(modifier = modifier.shimmerEffect())
}

/** Placeholder for a list row with a thumbnail and two lines of text, shown while a list first loads. */
@Composable
fun UriSkeletonListItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UriSkeleton(modifier = Modifier.size(56.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
            )
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriSkeletonPrev() {
    UrithiruTheme {
        Column {
            repeat(3) { UriSkeletonListItem() }
        }
    }
}

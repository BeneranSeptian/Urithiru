package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.Highlight

@Composable
fun UriLoader() {
    CircularProgressIndicator(
        modifier = Modifier.size(32.dp),
        color = Highlight.Highlight500
    )
}

@Preview(showBackground = true)
@Composable
private fun CirCularProgressDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        UriLoader()
    }
}
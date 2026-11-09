package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog

@Composable
fun CentralLoadingDialog() {
    Dialog(onDismissRequest = {}) {
        UriLoader()
    }
}

@Preview(showBackground = true)
@Composable
private fun CirCularProgressDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CentralLoadingDialog()
    }
}
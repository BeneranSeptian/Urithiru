package com.example.core.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun CircularProgressDialog() {
    Dialog(onDismissRequest = {}) {
        CircularProgressIndicator()
    }
}
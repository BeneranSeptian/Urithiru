package com.example.core_ui

data class BaseUiState(
    val showCentralLoading: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String? = null,
    val onDismissError: () -> Unit = {}
)

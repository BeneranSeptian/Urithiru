package com.septianbeneran.urithiru.core.ui.base

data class BaseScreenUiState(
    val showCentralLoading: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String? = null,
    val onDismissError: () -> Unit = {}
)
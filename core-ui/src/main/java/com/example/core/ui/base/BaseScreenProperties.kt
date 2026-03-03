package com.example.core.ui.base

import com.septianbeneran.template.core.util.permission.PermissionStatus

data class BaseScreenProperties(
    val handlePermission: (
        permissionSet: Set<String>,
        onPermissionResult: (PermissionStatus) -> Unit
    ) -> Unit
)
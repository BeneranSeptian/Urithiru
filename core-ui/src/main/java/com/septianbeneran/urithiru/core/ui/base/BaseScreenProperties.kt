package com.septianbeneran.urithiru.core.ui.base

import com.septianbeneran.urithiru.core.util.permission.PermissionStatus

data class BaseScreenProperties(
    val handlePermission: (
        permissionSet: Set<String>,
        onPermissionResult: (PermissionStatus) -> Unit
    ) -> Unit
)
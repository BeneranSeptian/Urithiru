package com.septianbeneran.urithiru.core.ui.util.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.septianbeneran.urithiru.core.ui.base.BaseScreenProperties
import com.septianbeneran.urithiru.core.util.permission.PermissionStatus
import com.septianbeneran.urithiru.core.util.permission.PermissionStatus.Denied
import com.septianbeneran.urithiru.core.util.permission.PermissionStatus.Granted

@Composable
fun rememberActionPermissionHandler(properties: BaseScreenProperties) = remember {
    ActionPermissionHandler(
        properties
    )
}

@Stable
class ActionPermissionHandler(private val properties: BaseScreenProperties) {

    fun startAction(
        permissions: Set<String>,
        onPermissionDenied: (() -> Unit)? = null,
        onPermissionGranted: (() -> Unit)? = null
    ) {

        properties.handlePermission(permissions) { permissionStatus: PermissionStatus ->
            when (permissionStatus) {
                is Granted -> onPermissionGranted?.invoke()
                is Denied -> onPermissionDenied?.invoke()
                else -> Unit
            }
        }
    }
}
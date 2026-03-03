package com.septianbeneran.template.core.util.permission

sealed class PermissionStatus {
    object Initial : PermissionStatus()
    object Granted : PermissionStatus()
    data class Denied(val permissionSet: Set<String>) : PermissionStatus()
}
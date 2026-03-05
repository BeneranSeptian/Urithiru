package com.septianbeneran.urithiru.core.util.permission

sealed class PermissionStatus {
    object Initial : PermissionStatus()
    object Granted : PermissionStatus()
    data class Denied(val permissionSet: Set<String>) : PermissionStatus()
}
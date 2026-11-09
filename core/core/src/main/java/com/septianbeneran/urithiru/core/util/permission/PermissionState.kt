package com.septianbeneran.urithiru.core.util.permission

sealed class PermissionState(
    open val isInitial: Boolean = true,
    open val permissionSet: Set<String> = setOf()
) {
    object Idle : PermissionState()

    data class Requesting(
        override val isInitial: Boolean,
        override val permissionSet: Set<String>
    ) : PermissionState(isInitial, permissionSet)

    data class GoToSetting(
        override val isInitial: Boolean,
        override val permissionSet: Set<String>,
        val isNavigatedToSetting: Boolean = false
    ) : PermissionState(isInitial, permissionSet)
}
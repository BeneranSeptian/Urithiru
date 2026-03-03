package com.septianbeneran.template.core.util.permission

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.toMutableStateList
import com.septianbeneran.template.core.util.permission.PermissionState.GoToSetting
import com.septianbeneran.template.core.util.permission.PermissionState.Idle
import com.septianbeneran.template.core.util.permission.PermissionState.Requesting
import com.septianbeneran.template.core.util.permission.PermissionStatus.Denied
import com.septianbeneran.template.core.util.permission.PermissionStatus.Granted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PermissionHandler @Inject constructor() {

    private val _permissionState: MutableStateFlow<PermissionState> = MutableStateFlow(Idle)
    val permissionState = _permissionState.asStateFlow()

    var permissionSetToGrant: Set<String> = emptySet()

    var permissionReceiver: ((PermissionStatus) -> Unit)? = null

    fun handlePermission(
        context: Context,
        permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
        permissionSet: Set<String>,
        onPermissionResult: (PermissionStatus) -> Unit
    ) {
        permissionSetToGrant = permissionSet
        permissionReceiver = onPermissionResult

        val isGranted = permissionSet.all { context.checkSelfPermission(it) == PERMISSION_GRANTED }

        if (isGranted) {
            onPermissionGranted()
        } else {
            val lastState = _permissionState.value
            _permissionState.value = Requesting(
                isInitial = lastState.isInitial,
                permissionSet = permissionSet
            )
            permissionLauncher.launch(permissionSet.toTypedArray())
        }
    }

    private fun onPermissionGranted() {
        permissionReceiver?.invoke(Granted)
        resetPermissionHandler()
    }

    private fun resetPermissionHandler() {
        _permissionState.value = Idle
    }

    fun onPermissionResult(
        permissionMap: Map<String, Boolean>
    ) {
        val lastState = permissionState.value
        val isInitial = lastState.isInitial
        val deniedPermissionList = permissionMap.filter { !it.value }.keys.toMutableStateList()

        if (permissionMap.isEmpty() || deniedPermissionList.isNotEmpty()) {
            if(lastState is GoToSetting && !isInitial) {
                onPermissionDenied(deniedPermissionList.toSet())
                resetPermissionHandler()
            } else {
                _permissionState.value = GoToSetting(
                    permissionSet = deniedPermissionList.toSet(),
                    isInitial = isInitial
                )
            }
        }
    }

    fun onPermissionDenied(permissionSet: Set<String>) {
        permissionReceiver?.invoke(Denied(permissionSet).copy(permissionSet = permissionSet))
        resetPermissionHandler()
    }

    fun onGoToSetting() {
        val lastState = _permissionState.value
        _permissionState.value = GoToSetting(
            permissionSet = lastState.permissionSet,
            isInitial = lastState.isInitial,
            isNavigatedToSetting = true
        )
    }
}
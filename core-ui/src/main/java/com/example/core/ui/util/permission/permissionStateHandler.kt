package com.example.core.ui.util.permission

import android.content.Context
import android.content.Intent
import android.net.Uri.fromParts
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.template.core.util.permission.PermissionHandler
import com.septianbeneran.template.core.util.permission.PermissionState.GoToSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun permissionStateHandler(
    permissionHandler: PermissionHandler,
    context: Context
): ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> {
    val permissionState = permissionHandler.permissionState.collectAsStateWithLifecycle().value

    val permissionResultLauncher = rememberLauncherForActivityResult(
        contract = RequestMultiplePermissions(),
        onResult = {
            permissionHandler.onPermissionResult(it)
        }
    )

    val settingLauncher = rememberLauncherForActivityResult(
        contract = StartActivityForResult(),
        onResult = {
            permissionHandler.permissionReceiver?.let {
                permissionHandler.handlePermission(
                    context = context,
                    permissionLauncher = permissionResultLauncher,
                    permissionSet = permissionHandler.permissionSetToGrant,
                    onPermissionResult = it
                )
            }
        }
    )

    if (permissionState is GoToSetting) {
        with(permissionState) {
            if (isInitial && !isNavigatedToSetting) {
                BasicAlertDialog(
                    onDismissRequest = { permissionHandler.onPermissionDenied(permissionSet) }
                ) {
                    Column() {
                        Text("$permissionSet is needed")
                        Button(
                            onClick = {
                                permissionHandler.onGoToSetting()
                                val intent = Intent(
                                    ACTION_APPLICATION_DETAILS_SETTINGS,
                                    fromParts("package", context.packageName, null)
                                )
                                settingLauncher.launch(intent)
                            }) {
                            Text("Go to Setting")
                        }
                    }
                }
            }
        }
    }

    return permissionResultLauncher
}
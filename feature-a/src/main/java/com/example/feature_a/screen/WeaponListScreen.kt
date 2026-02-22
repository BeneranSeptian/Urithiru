package com.example.feature_a.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core_ui.base.BaseScreen
import com.example.feature_a.screen.stateaction.WeaponListScreenAction
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.GetWeaponList
import com.example.feature_a.screen.stateaction.WeaponListScreenUiState
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.core.base.BaseViewModel

@Composable
fun WeaponListScreen(
    modifier: Modifier = Modifier,
    state: WeaponListScreenUiState,
    viewModel: BaseViewModel,
    onNavigateToWeaponDetail: (String) -> Unit = {},
    onAction: (WeaponListScreenAction) -> Unit
) {
    BaseScreen(
        viewModel = viewModel
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            when (val weaponListState = state.weaponListState) {
                is StateFailed -> {
                    Button(onClick = { onAction(GetWeaponList) }, modifier = Modifier.align(Alignment.Center)) {
                        Text("Retry")
                    }
                }
                is StateSuccess -> {
                    val weapons = weaponListState.value
                    if (weapons.isNullOrEmpty()) {
                        Text(text = "No weapons found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(weapons) { weapon ->
                                Text(
                                    text = weapon.name,
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .padding(16.dp)
                                        .clickable(true){
                                            onNavigateToWeaponDetail(weapon.id)
                                        }
                                )
                            }
                        }
                    }
                }
                is StateInitial -> {
                    Button(onClick = { onAction(GetWeaponList) }, modifier = Modifier.align(Alignment.Center)) {
                        Text("Get Weapons")
                    }
                }

                else -> Unit
            }
        }
    }

}
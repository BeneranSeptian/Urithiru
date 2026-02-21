package com.septianbeneran.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeaponList(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeaponList(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel()
) {
    val weaponListState by viewModel.weaponListState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = weaponListState) {
            is StateLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is StateFailed -> {
                Button(onClick = { viewModel.getWeaponList() }, modifier = Modifier.align(Alignment.Center)) {
                    Text("Retry")
                }
            }
            is StateSuccess -> {
                val weapons = state.value
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
                                        viewModel.getWeaponDetail(weapon.id)
                                    }
                            )
                        }
                    }
                }
            }
            is StateInitial -> {
                Button(onClick = { viewModel.getWeaponList() }, modifier = Modifier.align(Alignment.Center)) {
                    Text("Get Weapons")
                }
            }
        }
    }
}
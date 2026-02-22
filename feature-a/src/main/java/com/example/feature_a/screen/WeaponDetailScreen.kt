package com.example.feature_a.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.feature_a.screen.stateaction.WeaponDetailAction
import com.example.feature_a.screen.stateaction.WeaponDetailScreenUiState

@Composable
fun WeaponDetailScreen(
    state: WeaponDetailScreenUiState,
    onAction: (WeaponDetailAction) -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Weapon Detail for ID")
        }
    }
}
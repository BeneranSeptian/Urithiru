package com.example.feature_a.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core_ui.base.BaseScreen
import com.example.feature_a.screen.stateaction.WeaponDetailAction
import com.example.feature_a.screen.stateaction.WeaponDetailScreenUiState
import com.example.feature_a.viewmodel.WeaponDetailViewModel
import com.septianbeneran.template.core.base.BaseState
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core_entity.a.Weapon
import com.septianbeneran.template.core_entity.a.common.Attributes
import com.septianbeneran.template.core_entity.a.common.Scaling

@Composable
fun WeaponDetailScreen(
    viewModel: WeaponDetailViewModel,
    state: WeaponDetailScreenUiState,
    onAction: (WeaponDetailAction) -> Unit
) {
    BaseScreen(
        viewModel = viewModel
    ) {
        val weaponState = state.weaponDetailState
        if (weaponState is BaseState.StateSuccess) {
            weaponState.value?.let { weapon ->
                WeaponDetailContent(weapon = weapon)
            }
        } else if (weaponState is StateFailed) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Button(onClick = {
                    onAction(WeaponDetailAction.GetWeaponDetail(viewModel.weaponId))
                }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun WeaponDetailContent(weapon: Weapon) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = weapon.image,
            contentDescription = weapon.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = weapon.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = weapon.category,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        weapon.description?.let {
            Text(text = it, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        AttributeSection(title = "Attack", attributes = weapon.attack)
        AttributeSection(title = "Defence", attributes = weapon.defence)

        ScalingSection(title = "Scaling", scaling = weapon.scalesWith)
        AttributeSection(title = "Required Attributes", attributes = weapon.requiredAttributes)

        Text(
            text = "Weight: ${weapon.weight}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun AttributeSection(title: String, attributes: List<Attributes>) {
    if (attributes.isNotEmpty()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        attributes.forEach { attr ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = attr.name)
                Text(text = attr.amount.toString())
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ScalingSection(title: String, scaling: List<Scaling>) {
    if (scaling.isNotEmpty()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        scaling.forEach { scale ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = scale.name ?: "-")
                Text(text = scale.scaling ?: "-")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

package com.septianbeneran.urithiru.feature.a.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.entity.a.common.Attributes
import com.septianbeneran.urithiru.core.entity.a.common.Scaling
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_a.WeaponDetailRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.util.shimmerEffect
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailAction
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailAction.GetWeaponDetail
import com.septianbeneran.urithiru.feature.a.screen.stateaction.WeaponDetailScreenUiState
import com.septianbeneran.urithiru.feature.a.viewmodel.WeaponDetailViewModel

@FeatureRoute(
    routeParams = WeaponDetailRoute::class,
)
@Composable
fun WeaponDetailRoute(
    navigator: Navigator
) {
    val viewModel: WeaponDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel
    ) {
        WeaponDetailScreen(
            uiState = uiState,
            routeParam = viewModel.weaponId,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun WeaponDetailScreen(
    uiState: WeaponDetailScreenUiState,
    routeParam: String? = null,
    onAction: (WeaponDetailAction) -> Unit
) {
    when (val weaponState = uiState.weaponDetailState) {
        is StateSuccess -> {
            weaponState.value?.let { weapon ->
                WeaponDetailContent(weapon = weapon)
            }
        }

        is StateFailed -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    onAction(GetWeaponDetail(routeParam.orEmpty()))
                }) {
                    Text("Retry")
                }
            }
        }

        else -> {
            WeaponDetailSkeletonLoader()
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

@Composable
fun WeaponDetailSkeletonLoader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(16.dp))

        repeat(4) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        repeat(2) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(20.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

package com.example.feature_a.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.core_navigation.util.Navigator
import com.example.core_navigation.route.feature_a.WeaponDetailRoute
import com.example.core_ui.base.BaseScreen
import com.example.core_ui.util.NonceObserver
import com.example.feature_a.screen.stateaction.WeaponListNonce
import com.example.feature_a.screen.stateaction.WeaponListNonce.NavigateToWeaponDetail
import com.example.feature_a.screen.stateaction.WeaponListScreenAction
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.GetWeaponList
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.OnSearchButtonClick
import com.example.feature_a.screen.stateaction.WeaponListScreenAction.OnSearchWeaponTextChange
import com.example.feature_a.screen.stateaction.WeaponListScreenUiState
import com.example.feature_a.viewmodel.WeaponListViewModel
import com.septianbeneran.template.core.base.BaseState.StateFailed
import com.septianbeneran.template.core.base.BaseState.StateInitial
import com.septianbeneran.template.core.base.BaseState.StateLoading
import com.septianbeneran.template.core.base.BaseState.StateSuccess
import com.septianbeneran.template.core_entity.a.Weapon

@Composable
fun WeaponListRouteScreen(
    navigator: Navigator
) {
    val viewModel: WeaponListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel
    ) {
        WeaponListScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            onNonce = viewModel::sendNonce
        )
    }

    NonceObserver(
        nonce = viewModel.nonce,
        onNonce = { nonce ->
            when (nonce) {
                is NavigateToWeaponDetail -> {
                    navigator.navigate(WeaponDetailRoute(nonce.weaponId))
                }
            }
        }
    )
}

@Composable
fun WeaponListScreen(
    uiState: WeaponListScreenUiState,
    onAction: (WeaponListScreenAction) -> Unit,
    onNonce: (WeaponListNonce) -> Unit
) {
    Column {
        SearchBarSection(
            searchWeaponText = uiState.searchWeaponText,
            onSearchWeaponTextChange = { onAction(OnSearchWeaponTextChange(it)) },
            onSearchWeapon = { onAction(OnSearchButtonClick(uiState.searchWeaponText)) }
        )
        WeaponListSection(
            getWeaponList = { onAction(GetWeaponList) },
            onClickWeapon = { weaponId -> onNonce(NavigateToWeaponDetail(weaponId)) },
            onLoadNextPage = { onAction(WeaponListScreenAction.LoadNextPage) },
            state = uiState
        )
    }
}

@Composable
fun SearchBarSection(
    modifier: Modifier = Modifier,
    searchWeaponText: String, onSearchWeaponTextChange: (String) -> Unit,
    onSearchWeapon: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = searchWeaponText,
            onValueChange = onSearchWeaponTextChange,
            placeholder = { Text("Search weapon...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = onSearchWeapon,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Search")
        }
    }
}

@Composable
fun WeaponListSection(
    state: WeaponListScreenUiState,
    onLoadNextPage: () -> Unit,
    getWeaponList: () -> Unit,
    onClickWeapon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val weaponListState = state.weaponListState
        val weapons = state.weapons

        when {
            weaponListState is StateFailed && weapons.isEmpty() -> {
                Button(
                    onClick = getWeaponList,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Retry")
                }
            }

            weaponListState is StateInitial -> {
                Button(
                    onClick = getWeaponList,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Get Weapons")
                }
            }

            else -> {
                if (weapons.isEmpty() && weaponListState is StateSuccess) {
                    Text(text = "No weapons found", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(weapons) { index, weapon ->
                            if (index >= weapons.size - 1 && !state.isEndReached && weaponListState !is StateLoading) {
                                onLoadNextPage()
                            }
                            WeaponItem(
                                weapon = weapon,
                                onClick = { onClickWeapon(weapon.id) }
                            )
                        }

                        if (weaponListState is StateLoading && weapons.isNotEmpty()) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeaponItem(
    weapon: Weapon,
    onClick: () -> Unit
) {
    Card() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = weapon.image,
                contentDescription = weapon.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = weapon.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = weapon.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
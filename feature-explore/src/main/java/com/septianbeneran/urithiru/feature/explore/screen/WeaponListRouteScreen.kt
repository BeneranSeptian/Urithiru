package com.septianbeneran.urithiru.feature.explore.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateInitial
import com.septianbeneran.urithiru.core.base.BaseState.StateLoading
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureExploreNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.WeaponDetailRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.WeaponListRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriEmptyState
import com.septianbeneran.urithiru.core.ui.component.UriImage
import com.septianbeneran.urithiru.core.ui.component.UriListFooter
import com.septianbeneran.urithiru.core.ui.component.UriSearchBar
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriSkeletonListItem
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListNonce
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListNonce.NavigateToWeaponDetail
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListScreenAction
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListScreenAction.GetWeaponList
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListScreenAction.OnSearchButtonClick
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListScreenAction.OnSearchWeaponTextChange
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.WeaponListScreenUiState
import com.septianbeneran.urithiru.feature.explore.viewmodel.WeaponListViewModel

@FeatureRoute(
    routeParams = WeaponListRoute::class,
    graph = FeatureExploreNavGraph::class
)
@Composable
fun WeaponListRoute(
    navigator: Navigator
) {
    val viewModel: WeaponListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = "Weapon List")
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
            onSearchWeapon = { onAction(OnSearchButtonClick(uiState.searchWeaponText)) },
            onClearSearch = { onAction(GetWeaponList) }
        )
        Spacer(Modifier.height(8.dp))
        WeaponListSection(
            getWeaponList = { onAction(GetWeaponList) },
            onClickWeapon = { weaponId -> onNonce(NavigateToWeaponDetail(weaponId)) },
            onLoadNextPage = { onAction(WeaponListScreenAction.LoadNextPage) },
            getWeaponListLocal = { onAction(WeaponListScreenAction.GetWeaponListLocal) },
            state = uiState
        )
    }
}

@Composable
fun SearchBarSection(
    modifier: Modifier = Modifier,
    searchWeaponText: String, onSearchWeaponTextChange: (String) -> Unit,
    onSearchWeapon: () -> Unit,
    onClearSearch: () -> Unit
) {
    UriSearchBar(
        modifier = modifier,
        value = searchWeaponText,
        onValueChange = onSearchWeaponTextChange,
        onSearch = onSearchWeapon,
        placeholder = "Search weapon...",
        onClear = onClearSearch
    )
}

@Composable
fun WeaponListSection(
    state: WeaponListScreenUiState,
    onLoadNextPage: () -> Unit,
    getWeaponList: () -> Unit,
    getWeaponListLocal: () -> Unit,
    onClickWeapon: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val weaponListState = state.weaponListState
        val weapons = state.weapons
        val weaponListLocal = state.weaponListLocal

        when {
            (weaponListLocal.isNotEmpty()) -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(weaponListLocal) { index, weapon ->
                        WeaponItem(
                            weapon = weapon,
                            onClick = { onClickWeapon(weapon.id) }
                        )
                    }
                }
            }

            weaponListState is StateFailed && weapons.isEmpty() -> {
                UriErrorState(
                    error = weaponListState.failed,
                    onRetry = getWeaponList
                )
            }

            weaponListState is StateLoading && weapons.isEmpty() -> {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    repeat(6) { UriSkeletonListItem() }
                }
            }

            weaponListState is StateInitial -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    Button(
                        onClick = getWeaponList,
                        modifier = Modifier
                    ) {
                        Text("Get Weapons From API")
                    }

                    Button(
                        onClick = getWeaponListLocal,
                        modifier = Modifier
                    ) {
                        Text("Get Weapons From Local")
                    }
                }
            }

            else -> {
                if (weapons.isEmpty() && weaponListState is StateSuccess) {
                    UriEmptyState(
                        title = "No weapons found",
                        message = "Try a different name."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(weapons) { index, weapon ->
                            if (index >= weapons.size - 1 && !state.isEndReached && weaponListState is StateSuccess) {
                                onLoadNextPage()
                            }
                            WeaponItem(
                                weapon = weapon,
                                onClick = { onClickWeapon(weapon.id) }
                            )
                        }

                        item {
                            UriListFooter(
                                isLoading = weaponListState is StateLoading,
                                error = (weaponListState as? StateFailed)?.failed,
                                onRetry = onLoadNextPage
                            )
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
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UriImage(
                modifier = Modifier.size(64.dp),
                model = weapon.image,
                contentDescription = weapon.name,
                shape = RoundedCornerShape(8.dp)
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
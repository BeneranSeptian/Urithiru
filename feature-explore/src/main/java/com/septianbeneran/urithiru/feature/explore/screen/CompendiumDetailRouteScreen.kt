package com.septianbeneran.urithiru.feature.explore.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.base.BaseState.StateFailed
import com.septianbeneran.urithiru.core.base.BaseState.StateSuccess
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore.CompendiumDetailRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriSkeleton
import com.septianbeneran.urithiru.feature.explore.R
import com.septianbeneran.urithiru.feature.explore.screen.detail.DetailSections
import com.septianbeneran.urithiru.feature.explore.screen.detail.DetailTitle
import com.septianbeneran.urithiru.feature.explore.screen.feed.EntryImage
import com.septianbeneran.urithiru.feature.explore.screen.feed.isArtwork
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.CompendiumDetailAction
import com.septianbeneran.urithiru.feature.explore.screen.stateaction.CompendiumDetailUiState
import com.septianbeneran.urithiru.feature.explore.viewmodel.CompendiumDetailViewModel

private val HeroHeight = 320.dp
private val SheetOverlap = 28.dp

@FeatureRoute(
    routeParams = CompendiumDetailRoute::class
)
@Composable
fun CompendiumDetailRoute(
    navigator: Navigator
) {
    val viewModel: CompendiumDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel,
        contentPadding = PaddingValues(0.dp)
    ) {
        CompendiumDetailScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            onBack = { navigator.navigateUp() }
        )
    }
}

@Composable
fun CompendiumDetailScreen(
    uiState: CompendiumDetailUiState,
    onAction: (CompendiumDetailAction) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState.detailState) {
            is StateSuccess -> state.value?.let { DetailContent(entry = it) }

            is StateFailed -> Column(modifier = Modifier.padding(top = 72.dp)) {
                UriErrorState(
                    error = state.failed,
                    onRetry = { onAction(CompendiumDetailAction.Retry) }
                )
            }

            else -> DetailSkeleton()
        }

        BackButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onBack
        )
    }
}

@Composable
private fun DetailContent(entry: CompendiumEntry) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Hero(entry = entry)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = HeroHeight - SheetOverlap)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            DetailTitle(entry = entry)
            DetailSections(entry = entry)
        }
    }
}

@Composable
private fun Hero(entry: CompendiumEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        EntryImage(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (entry.isArtwork) Modifier
                    else Modifier.padding(start = 48.dp, end = 48.dp, top = 56.dp, bottom = SheetOverlap + 24.dp)
                ),
            entry = entry
        )
        if (entry.isArtwork) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))
                    )
            )
        }
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 4.dp
    ) {
        Icon(
            modifier = Modifier
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(CoreUiR.drawable.arrow_back),
            contentDescription = stringResource(R.string.explore_back)
        )
    }
}

@Composable
private fun DetailSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        UriSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeroHeight - SheetOverlap)
        )
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) {
                    UriSkeleton(
                        modifier = Modifier
                            .size(width = 72.dp, height = 24.dp)
                            .clip(CircleShape)
                    )
                }
            }
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    UriSkeleton(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            repeat(4) { index ->
                UriSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(if (index == 3) 0.6f else 1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        }
    }
}

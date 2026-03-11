package com.septianbeneran.urithiru.feature.b.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.septianbeneran.urithiru.core.entity.b.OnBoardingPageData
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.OnBoardingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.PersonalizeExperienceRouteParams
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriDot
import com.septianbeneran.urithiru.core.ui.component.UriMediaPlaceHolder
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTypography
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenAction
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenNonce
import com.septianbeneran.urithiru.feature.b.screen.stateaction.OnBoardingScreenNonce.NavigateToPersonalizeExperienceScreen
import com.septianbeneran.urithiru.feature.b.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.launch

@FeatureRoute(
    routeParams = OnBoardingRoute::class,
)
@Composable
fun OnBoardingRoute(
    navigator: Navigator
) {
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    NonceObserver(
        nonce = viewModel.nonce,
        onNonce = { nonce ->
            when (nonce) {
                NavigateToPersonalizeExperienceScreen -> {
                    navigator.navigate(
                        route = PersonalizeExperienceRouteParams,
                        popUpTo = OnBoardingRoute,
                        inclusive = true
                    )
                }
            }
        }
    )

    BaseScreen(
        viewModel = viewModel,
        contentPadding = PaddingValues(),
        modifier = Modifier,
        isUseSystembarsPadding = false
    ) { properties ->
        OnBoardingScreen(
            pages = uiState.value.onBoardingPageDataList,
            onNonce = viewModel::sendNonce,
            onAction = viewModel::onAction
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    pages: List<OnBoardingPageData> = emptyList(),
    onAction: (OnBoardingScreenAction) -> Unit = {},
    onNonce: (OnBoardingScreenNonce) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { position ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(3f),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pages[position].imgUrl)
                                .crossfade(true)
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = "Onboarding Media",
                            modifier = Modifier.fillMaxSize(),
                            loading = { UriMediaPlaceHolder(modifier = Modifier.fillMaxSize()) },
                            error = { UriMediaPlaceHolder(modifier = Modifier.fillMaxSize()) },
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .alpha(0f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(pages.size) { UriDot() }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    BottomSection(
                        modifier = Modifier.weight(1f),
                        pageData = pages[position]
                    )
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(3f))

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        UriDot(isFilled = index == pagerState.currentPage)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        UriButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = if (isLastPage) "Get Started" else "Next",
            onClick = {
                if (isLastPage) {
                    println("masuk last page")
                    onNonce(NavigateToPersonalizeExperienceScreen)
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        )
    }
}

@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    pageData: OnBoardingPageData
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = pageData.title,
            style = UrithiruTypography.headlineLarge,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = pageData.description,
            style = UrithiruTypography.bodySmall,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreenPreview() {
    UrithiruTheme() {
        Box() {
            OnBoardingScreen()
        }
    }
}
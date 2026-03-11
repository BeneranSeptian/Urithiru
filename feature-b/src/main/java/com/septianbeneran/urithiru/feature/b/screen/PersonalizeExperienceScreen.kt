package com.septianbeneran.urithiru.feature.b.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.LandingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_b.PersonalizeExperienceRouteParams
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.component.UriProgressBar
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.Highlight.Highlight100
import com.septianbeneran.urithiru.core.ui.theme.Neutral
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light100
import com.septianbeneran.urithiru.core.ui.theme.Neutral.Light.Light500
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTypography
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.b.screen.PersonalizeExperienceScreenProperties.InterestItem
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenAction
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenAction.OnClickInterest
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenNonce
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenNonce.NavigateTo
import com.septianbeneran.urithiru.feature.b.screen.stateaction.PersonalizeExperienceScreenState
import com.septianbeneran.urithiru.feature.b.viewmodel.PersonalizeExperienceViewModel

@FeatureRoute(
    routeParams = PersonalizeExperienceRouteParams::class,
)
@Composable
fun PersonalizeExperienceScreen(navigator: Navigator) {
    val viewModel: PersonalizeExperienceViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel
    ) {
        PersonalizeExperienceScreen(
            uiState = uiState.value,
            onAction = viewModel::onAction,
            onNonce = viewModel::sendNonce
        )
    }

    NonceObserver(
        nonce = viewModel.nonce
    ) { nonce ->
        when (nonce) {
            NavigateTo -> {
                println("masuk observer")
                navigator.navigate(
                    route = LandingRoute,
                    popUpTo = PersonalizeExperienceRouteParams,
                    inclusive = true
                )
            }
        }
    }
}

@Composable
fun PersonalizeExperienceScreen(
    uiState: PersonalizeExperienceScreenState,
    onAction: (PersonalizeExperienceScreenAction) -> Unit,
    onNonce: (PersonalizeExperienceScreenNonce) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        UriProgressBar(progress = 0.5f)
        TitleSection(modifier = Modifier.padding(vertical = 41.dp))
        ListSection(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            interestList = uiState.interestList,
            onClickInterest = { interest, index -> onAction(OnClickInterest(interest, index)) }
        )
        UriButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Next",
            onClick = {
                println("masuk click button")
                onNonce(NavigateTo)
            }
        )
    }
}

@Composable
private fun TitleSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personalise your experience here in Urithiru Project",
            style = UrithiruTypography.headlineLarge,
            maxLines = 2
        )
        Text(
            text = "Choose your interest.",
            style = UrithiruTypography.bodySmall,
            color = Neutral.Dark.Dark200
        )
    }
}

@Composable
fun ListSection(
    modifier: Modifier = Modifier,
    interestList: List<InterestItem>,
    onClickInterest: (InterestItem, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(interestList) { index, interest ->

            UriListItem(
                title = interest.value,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (interest.isSelected) Highlight100 else Light100)
                    .border(
                        width = 1.dp,
                        color = if (interest.isSelected) Highlight100 else Light500,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(true, onClick = { onClickInterest(interest, index) }),
                trailingContent = {
                    if (interest.isSelected) {
                        Box(
                            modifier = Modifier.size(16.dp),
                            contentAlignment = Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.check_rounded),
                                contentDescription = null,
                                tint = Highlight.Highlight500,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonalizeExperienceScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        PersonalizeExperienceScreen(
            uiState = PersonalizeExperienceScreenState(),
            onAction = {},
            onNonce = {}
        )
    }
}

object PersonalizeExperienceScreenProperties {
    data class InterestItem(
        val value: String,
        val isSelected: Boolean
    )
}
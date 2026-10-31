package com.septianbeneran.urithiru.feature.profile.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.base.BaseState
import com.septianbeneran.urithiru.core.entity.auth.User
import com.septianbeneran.urithiru.core.entity.auth.UserProfile
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureProfileNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_auth.LoginRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_onboarding.OnBoardingRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_profile.ProfileRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties
import com.septianbeneran.urithiru.core.ui.component.UriDialog
import com.septianbeneran.urithiru.core.ui.component.UriDialogProperties
import com.septianbeneran.urithiru.core.ui.component.UriErrorState
import com.septianbeneran.urithiru.core.ui.component.UriImage
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.component.UriSkeleton
import com.septianbeneran.urithiru.core.ui.component.UriThemePicker
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileAction
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileNonce
import com.septianbeneran.urithiru.feature.profile.screen.stateaction.ProfileUiState
import com.septianbeneran.urithiru.feature.profile.viewmodel.ProfileViewModel

@FeatureRoute(
    routeParams = ProfileRoute::class,
    graph = FeatureProfileNavGraph::class
)
@Composable
fun ProfileRoute(
    navigator: Navigator
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = "Profile", backIcon = null)
    ) {
        ProfileScreen(
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }

    NonceObserver(viewModel.nonce) { nonce ->
        when (nonce) {
            ProfileNonce.NavigateToLogin -> navigator.navigate(LoginRoute(returnToPrevious = true))
            ProfileNonce.NavigateToOnboarding -> navigator.navigate(OnBoardingRoute)
        }
    }
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onAction: (ProfileAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (uiState.isSessionLoaded) {
            val user = uiState.user
            if (user != null) {
                UserHeader(user = user)
                ProfileSection("Account details") {
                    AccountDetails(
                        profileState = uiState.profileState,
                        onRetry = { onAction(ProfileAction.OnRetryProfileClick) }
                    )
                }
            } else {
                GuestCard(onSignInClick = { onAction(ProfileAction.OnSignInClick) })
            }
        }

        ProfileSection("Appearance") {
            UriThemePicker(
                selected = uiState.themeMode,
                onSelect = { onAction(ProfileAction.OnThemeModeSelect(it)) }
            )
        }

        ProfileSection("More") {
            UriListItem(
                modifier = Modifier.clickable { onAction(ProfileAction.OnReplayOnboardingClick) },
                title = "Replay onboarding",
                description = "See the first-launch tour again",
                trailingContent = {
                    Icon(
                        painter = painterResource(R.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        if (uiState.user != null) {
            UriButton(
                modifier = Modifier.fillMaxWidth(),
                type = UriButtonProperties.Type.Secondary,
                text = "Log out",
                onClick = { onAction(ProfileAction.OnLogoutClick) }
            )
        }
    }

    if (uiState.showLogoutDialog) {
        UriDialog(
            textTitle = "Log out?",
            textDescription = "You'll need to sign in again to see your account details.",
            type = UriDialogProperties.Type.Confirmation(
                primaryButtonText = "Log out",
                secondaryButtonText = "Cancel",
                onPrimaryButtonClick = { onAction(ProfileAction.OnLogoutConfirm) },
                onSecondaryButtonClick = { onAction(ProfileAction.OnLogoutDismiss) }
            ),
            onDismissRequest = { onAction(ProfileAction.OnLogoutDismiss) },
            isCancelable = true
        )
    }
}

@Composable
private fun UserHeader(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UriImage(
            modifier = Modifier.size(72.dp),
            model = user.image,
            contentDescription = null,
            shape = CircleShape
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = UrithiruTheme.typography.h3,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "@${user.username}",
                style = UrithiruTheme.typography.bodyM,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = user.email,
                style = UrithiruTheme.typography.bodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GuestCard(onSignInClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "You're browsing as a guest",
                style = UrithiruTheme.typography.h4,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Sign in with the DummyJSON demo account to see your account details and the token refresh.",
                style = UrithiruTheme.typography.bodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UriButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = "Sign in",
                onClick = onSignInClick
            )
        }
    }
}

@Composable
private fun AccountDetails(
    profileState: BaseState<UserProfile>,
    onRetry: () -> Unit
) {
    when (profileState) {
        is BaseState.StateFailed -> UriErrorState(error = profileState.failed, onRetry = onRetry)
        is BaseState.StateSuccess -> {
            val details = profileState.value?.toDetailRows().orEmpty()
            details.forEach { (label, value) ->
                UriListItem(title = label, description = value)
            }
            Text(
                text = "Loaded from auth/me with your access token. When it expires, it's refreshed and the request is retried.",
                style = UrithiruTheme.typography.bodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        BaseState.StateInitial, BaseState.StateLoading -> repeat(3) {
            UriSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

private fun UserProfile.toDetailRows(): List<Pair<String, String>> = listOfNotNull(
    phone?.let { "Phone" to it },
    birthDate?.let { "Birth date" to it },
    age?.let { "Age" to it.toString() },
    gender?.let { "Gender" to it.replaceFirstChar(Char::uppercase) },
    listOfNotNull(jobTitle, company).takeIf { it.isNotEmpty() }?.let { "Work" to it.joinToString(" at ") },
    listOfNotNull(city, country).takeIf { it.isNotEmpty() }?.let { "Location" to it.joinToString(", ") }
)

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = UrithiruTheme.typography.h5,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

private val previewUser = User(
    id = 1,
    username = "emilys",
    email = "emily.johnson@x.dummyjson.com",
    firstName = "Emily",
    lastName = "Johnson",
    image = ""
)

@Preview(showBackground = true)
@Composable
private fun ProfileScreenSignedInPrev() {
    UrithiruTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                isSessionLoaded = true,
                user = previewUser,
                profileState = BaseState.StateSuccess(
                    UserProfile(
                        user = previewUser,
                        phone = "+81 965-431-3024",
                        birthDate = "1996-5-30",
                        age = 28,
                        gender = "female",
                        jobTitle = "Sales Manager",
                        company = "Dooley, Kozey and Cronin",
                        city = "Phoenix",
                        country = "United States"
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenGuestPrev() {
    UrithiruTheme {
        ProfileScreen(
            uiState = ProfileUiState(isSessionLoaded = true),
            onAction = {}
        )
    }
}

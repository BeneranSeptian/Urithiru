package com.septianbeneran.urithiru.feature.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.septianbeneran.urithiru.core.navigation.annotation.FeatureRoute
import com.septianbeneran.urithiru.core.navigation.graph.FeatureAuthNavGraph
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_auth.LoginRoute
import com.septianbeneran.urithiru.core.navigation.util.Navigator
import com.septianbeneran.urithiru.core.remote.entity.AppError
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.base.BaseScreen
import com.septianbeneran.urithiru.core.ui.component.UriButton
import com.septianbeneran.urithiru.core.ui.component.UriButtonProperties.Type.Tertiary
import com.septianbeneran.urithiru.core.ui.component.UriTextField
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.util.NonceObserver
import com.septianbeneran.urithiru.core.ui.util.extension.toMessage
import com.septianbeneran.urithiru.feature.auth.R
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginAction
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginNonce
import com.septianbeneran.urithiru.feature.auth.screen.stateaction.LoginUiState
import com.septianbeneran.urithiru.feature.auth.viewmodel.LoginViewModel

@FeatureRoute(
    routeParams = LoginRoute::class,
    graph = FeatureAuthNavGraph::class
)
@Composable
fun LoginRoute(
    navigator: Navigator
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = viewModel,
        topAppBarArgs = defaults(title = stringResource(CoreUiR.string.common_sign_in))
    ) {
        LoginScreen(
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }

    NonceObserver(viewModel.nonce) { nonce ->
        when (nonce) {
            LoginNonce.NavigateToHome -> navigator.navigateToHome()
            LoginNonce.NavigateBack -> navigator.navigateUp()
        }
    }
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_welcome_back),
            style = UrithiruTheme.typography.h1,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.auth_intro),
            style = UrithiruTheme.typography.bodyM,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        UriTextField(
            value = uiState.username,
            onValueChange = { onAction(LoginAction.OnUsernameChange(it)) },
            label = stringResource(CoreUiR.string.common_username),
            enabled = !uiState.isLoading
        )
        UriTextField(
            value = uiState.password,
            onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
            label = stringResource(CoreUiR.string.common_password),
            isPassword = true,
            enabled = !uiState.isLoading,
            errorText = uiState.error?.toLoginMessage(),
            imeAction = ImeAction.Done,
            onImeAction = {
                keyboardController?.hide()
                onAction(LoginAction.OnSignInClick)
            }
        )
        UriButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(if (uiState.isLoading) R.string.auth_signing_in else CoreUiR.string.common_sign_in),
            onClick = {
                keyboardController?.hide()
                onAction(LoginAction.OnSignInClick)
            }
        )
        UriButton(
            modifier = Modifier.fillMaxWidth(),
            type = Tertiary,
            text = stringResource(R.string.auth_use_demo_account),
            onClick = { onAction(LoginAction.OnUseDemoAccountClick) }
        )
    }
}

@Composable
private fun AppError.toLoginMessage(): String = when (this) {
    is AppError.Http, AppError.Unauthorized -> stringResource(R.string.auth_invalid_credentials)
    else -> stringResource(toMessage())
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPrev() {
    UrithiruTheme {
        LoginScreen(
            uiState = LoginUiState(username = "emilys", password = "wrong", error = AppError.Http(400, null)),
            onAction = {}
        )
    }
}

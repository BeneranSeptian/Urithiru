package com.septianbeneran.urithiru.core.navigation.routeparams.feature_auth

import kotlinx.serialization.Serializable

/** Set [returnToPrevious] to go back to the screen that opened login after signing in, instead of to home. */
@Serializable
data class LoginRoute(val returnToPrevious: Boolean = false)

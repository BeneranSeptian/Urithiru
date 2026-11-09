package com.septianbeneran.urithiru.core.annotation

import jakarta.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

/** The theme mode the app uses until the user picks one. Provided by `:app`. */
@Qualifier
@Retention(BINARY)
annotation class DefaultThemeMode

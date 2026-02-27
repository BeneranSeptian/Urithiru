package com.example.core.navigation.annotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

/**
 * Annotation to define a navigation route for a feature.
 *
 * @property route Serialized object or data class declared in core-navigation to register the route.
 * @property graph Will be used as the start destination of the feature when added.
 */
@Target(FUNCTION)
@Retention(SOURCE)
annotation class FeatureRoute (
    val route: KClass<*>,
    val graph: KClass<*> = Nothing::class
)
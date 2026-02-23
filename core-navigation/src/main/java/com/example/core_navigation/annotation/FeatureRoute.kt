package com.example.core_navigation.annotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Target(FUNCTION)
@Retention(SOURCE)
annotation class FeatureRoute (
    val route: KClass<*>,
//    val graph: KClass<*>
)
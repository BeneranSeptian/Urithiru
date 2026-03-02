package com.example.core.navigation.annotation

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION, ANNOTATION_CLASS)
@Retention(BINARY)
annotation class FeatureGraph
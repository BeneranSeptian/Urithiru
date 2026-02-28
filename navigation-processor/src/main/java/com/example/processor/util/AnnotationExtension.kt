package com.example.processor.util

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType

internal object AnnotationExtension {
    inline fun <reified T> KSAnnotation.findArgumentValue(name: String): T? =
        arguments.find { it.name?.asString() == name }?.value as T?

    fun KSAnnotation.getRouteParams() = findArgumentValue<KSType>("routeParams")!!

    fun KSAnnotation.getGraph() = findArgumentValue<KSType>("graph")!!
}
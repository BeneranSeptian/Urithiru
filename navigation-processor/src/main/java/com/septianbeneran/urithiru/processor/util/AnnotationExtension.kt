package com.septianbeneran.urithiru.processor.util

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType

internal object AnnotationExtension {
    inline fun <reified T> KSAnnotation.findArgumentValue(name: String): T? =
        arguments.find { it.name?.asString() == name }?.value as? T

    fun KSAnnotation.getRouteParams(): KSType? = findArgumentValue<KSType>("routeParams")

    fun KSAnnotation.getGraph(): KSType? = findArgumentValue<KSType>("graph")
}

package com.septianbeneran.urithiru.processor.model

import com.google.devtools.ksp.symbol.KSType

data class RouteData(
    val routeParams: KSType,
    val graphClass: KSType
)
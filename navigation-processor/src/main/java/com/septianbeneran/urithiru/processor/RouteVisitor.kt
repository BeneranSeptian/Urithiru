package com.septianbeneran.urithiru.processor

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.septianbeneran.urithiru.processor.generator.generateRouteSpec
import com.septianbeneran.urithiru.processor.model.RouteData
import com.squareup.kotlinpoet.FileSpec

class RouteVisitor : KSDefaultVisitor<RouteData, FileSpec>() {

    override fun defaultHandler(node: KSNode, data: RouteData): FileSpec {
        val function = (node as KSFunctionDeclaration)
        val sourcePackageName = function.packageName.asString()
        val simpleName = function.simpleName.asString()

        val fileSpec = FileSpec.builder(sourcePackageName, simpleName)
            .generateRouteSpec(
                functionName = simpleName,
                packageName = sourcePackageName,
                routeParams = data.routeParams
            ).build()

        return fileSpec
    }
}

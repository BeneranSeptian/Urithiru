package com.example.processor

import com.example.processor.generator.generateRouteCode
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid

class RouteVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val generatedRouteSet: MutableSet<String>
) : KSVisitorVoid() {
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

        val routeType = getRouteTypeFromAnnotation(function)
        if (routeType == null) {
            logger.error("FeatureRoute annotation missing 'route' argument", function)
            return
        }

        val routeId = routeType.declaration.qualifiedName?.asString()
            ?: routeType.toString()

        if (generatedRouteSet.contains(routeId)) {
            logger.error(
                "Duplicate Route Error: The route '$routeId' is already bound to another function.",
                function
            )
            return
        }

        generatedRouteSet.add(routeId)
        generateRouteCode(codeGenerator,function, routeType)
    }

    private fun getRouteTypeFromAnnotation(function: KSFunctionDeclaration): KSType? {
        val annotation = function.annotations.firstOrNull {
            it.shortName.asString() == "FeatureRoute"
        } ?: return null

        val routeArg = annotation.arguments.find { it.name?.asString() == "route" }
        return routeArg?.value as? KSType
    }
}


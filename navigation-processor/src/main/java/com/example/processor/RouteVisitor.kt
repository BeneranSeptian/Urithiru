package com.example.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class RouteVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : KSVisitorVoid() {

    // This function is automatically called when the visitor visits a Function
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

        // 1. Extract the KClass from the annotation
        val routeType = getRouteTypeFromAnnotation(function)
        if (routeType == null) {
            logger.error("FeatureRoute annotation missing 'route' argument", function)
            return
        }

        // 2. Generate the code (Logic moved here)
        generateRouteCode(function, routeType)
    }

    private fun generateRouteCode(function: KSFunctionDeclaration, routeType: KSType) {
        val packageName = function.packageName.asString()
        val targetPackage = packageName.replace(".screen", ".route")

        val composableName = function.simpleName.asString()
        val generatedFuncName = composableName.replaceFirstChar { it.lowercase() }

        // --- FIX START: Define the function as a MemberName ---
        // This tells KotlinPoet: "I want to reference a function, not a class"
        val targetComposableMember = MemberName(packageName, composableName)
        // --- FIX END ---

        // KotlinPoet Setup
        val navGraphBuilderClass = ClassName("androidx.navigation", "NavGraphBuilder")
        val navigatorClass = ClassName("com.example.core.navigation.util", "Navigator")
        val composableExtension = MemberName("androidx.navigation.compose", "composable")

        val fileSpec = FileSpec.builder(targetPackage, "${composableName}Generated")
            .addFunction(
                FunSpec.builder(generatedFuncName)
                    .addModifiers(KModifier.INTERNAL)
                    .receiver(navGraphBuilderClass)
                    .addParameter("navigator", navigatorClass)
                    .beginControlFlow("%M<%T>", composableExtension, routeType.toClassName())

                    // --- FIX START: Use %M and the MemberName variable ---
                    .addStatement("%M(navigator)", targetComposableMember)
                    // --- FIX END ---

                    .endControlFlow()
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, function.containingFile!!))
    }

    private fun getRouteTypeFromAnnotation(function: KSFunctionDeclaration): KSType? {
        val annotation = function.annotations.firstOrNull {
            it.shortName.asString() == "FeatureRoute"
        } ?: return null

        val routeArg = annotation.arguments.find { it.name?.asString() == "route" }
        return routeArg?.value as? KSType
    }
}


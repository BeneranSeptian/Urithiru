package com.example.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

fun generateRouteCode(
    codeGenerator: CodeGenerator,
    function: KSFunctionDeclaration,
    routeType: KSType
) {
    val packageName = function.packageName.asString()
    val targetPackage = packageName.replace(".screen", ".route")

    val composableName = function.simpleName.asString()
    val generatedFuncName = composableName.replaceFirstChar { it.lowercase() }

    val targetComposableMember = MemberName(packageName, composableName)

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
                .addStatement("%M(navigator)", targetComposableMember)
                .endControlFlow()
                .build()
        )
        .build()

    fileSpec.writeTo(codeGenerator, Dependencies(true, function.containingFile!!))
}
package com.example.processor.generator

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName

fun FileSpec.Builder.generateRouteSpec(
    functionName: String,
    packageName: String,
    routeParams: KSType
): FileSpec.Builder {
    val targetPackage = packageName.replace(".screen", ".route")
    val generatedFuncName = functionName.replaceFirstChar { it.lowercase() } + "Generated"
    val targetComposableMember = MemberName(packageName, functionName)

    val navGraphBuilderClass = ClassName("androidx.navigation", "NavGraphBuilder")
    val navigatorClass = ClassName("com.example.core.navigation.util", "Navigator")
    val composableExtension = MemberName("androidx.navigation.compose", "composable")

    val fileSpec = FileSpec.builder(targetPackage, "${functionName}Generated")
        .addFunction(
            FunSpec.builder(generatedFuncName)
                .addModifiers(KModifier.INTERNAL)
                .receiver(navGraphBuilderClass)
                .addParameter("navigator", navigatorClass)
                .beginControlFlow("%M<%T>", composableExtension, routeParams.toClassName())
                .addStatement("%M(navigator)", targetComposableMember)
                .endControlFlow()
                .build()
        )
    return fileSpec
}

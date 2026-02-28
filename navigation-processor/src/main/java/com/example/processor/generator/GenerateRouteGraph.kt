package com.example.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.Locale

fun generateRouteGraph(
    graphClassName: TypeName,
    startDestinationRouteParamsName: TypeName,
    routeFiles: List<FileSpec>,
    packageName: String,
    codeGenerator: CodeGenerator
) {
    val navGraphBuilder = ClassName("androidx.navigation", "NavGraphBuilder")
    val navigation = MemberName("androidx.navigation", "navigation")
    val navigator = ClassName("com.example.core.navigation.util", "Navigator")
    val featureGraphAnnotation = ClassName("com.example.core.navigation.annotation", "FeatureGraph")

    val fileName = graphClassName.toString().substringAfterLast('.')
    val functionName = fileName.replaceFirstChar { it.lowercase() }

    val graphFunction = FunSpec.builder(functionName)
        .receiver(navGraphBuilder)
        .addParameter("navigator", navigator)
        .addModifiers(KModifier.PUBLIC)
        .addAnnotation(featureGraphAnnotation)
        .apply {
            beginControlFlow(
                "%M<%T>(startDestination = %T)",
                navigation,
                graphClassName,
                startDestinationRouteParamsName
            )

            routeFiles.forEach { route ->
                val routeMember = MemberName(route.packageName, route.funSpecs.first().name)
                addStatement("%M(navigator)", routeMember)
            }

            endControlFlow()
        }
        .build()

    FileSpec
        .builder(
            packageName = packageName,
            fileName = fileName
        )
        .addFunction(graphFunction)
        .build()
        .writeTo(codeGenerator, true)
}
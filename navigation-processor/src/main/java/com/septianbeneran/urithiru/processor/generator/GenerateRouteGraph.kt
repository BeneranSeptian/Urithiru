package com.septianbeneran.urithiru.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.septianbeneran.urithiru.processor.util.Constant.ANNOTATION_PACKAGE_NAME
import com.septianbeneran.urithiru.processor.util.Constant.FEATURE_GRAPH_ANNOTATION
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_NAME
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_PACKAGE
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.writeTo

fun generateRouteGraph(
    graphClassName: TypeName,
    startDestinationRouteParamsName: TypeName,
    routeFiles: List<FileSpec>,
    packageName: String,
    codeGenerator: CodeGenerator
) {
    val navGraphBuilder = ClassName("androidx.navigation", "NavGraphBuilder")
    val navigation = MemberName("androidx.navigation", "navigation")

    val navigator = ClassName(NAVIGATOR_CLASS_PACKAGE, NAVIGATOR_CLASS_NAME)
    val featureGraphAnnotation = ClassName(ANNOTATION_PACKAGE_NAME, FEATURE_GRAPH_ANNOTATION)

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
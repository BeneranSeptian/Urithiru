package com.septianbeneran.urithiru.processor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_NAME
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_PACKAGE
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.writeTo

fun generateGraphRegistry(
    graphFunctions: List<KSFunctionDeclaration>,
    packageName: String,
    codeGenerator: CodeGenerator
) {
    val navGraphBuilder = ClassName("androidx.navigation", "NavGraphBuilder")
    val navigator = ClassName(NAVIGATOR_CLASS_PACKAGE, NAVIGATOR_CLASS_NAME)

    val registryFunction = FunSpec.builder("registerAllFeatureGraphs")
        .receiver(navGraphBuilder)
        .addParameter("navigator", navigator)
        .addModifiers(KModifier.PUBLIC)
        .apply {
            graphFunctions.forEach { function ->
                val funcPackage = function.packageName.asString()
                val funcName = function.simpleName.asString()
                val functionMember = MemberName(funcPackage, funcName)

                addStatement("%M(navigator)", functionMember)
            }
        }
        .build()

    FileSpec.builder(packageName, "FeatureGraphRegistry")
        .addFunction(registryFunction)
        .build()
        .writeTo(codeGenerator, true)
}
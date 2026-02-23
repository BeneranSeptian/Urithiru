package com.example.navigation_processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class FeatureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var isInvoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("!!! KSP IS RUNNING !!!")
        if (isInvoked) return emptyList()

        val annotationName = "com.example.core_navigation.annotation.FeatureRoute"

        val symbols = resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        logger.warn("Disini ${symbols.size} symbols for annotation: $annotationName")        // If no symbols are found, exit early
        if (symbols.isEmpty()) return emptyList()

        val funSpecBuilder = FunSpec.builder("testNavigationSetup")
            .addStatement("println(%S)", "KSP is alive! Found these annotated functions:")

        for (function in symbols) {
            val functionName = function.simpleName.asString()
            funSpecBuilder.addStatement("println(%S)", " -> $functionName")
        }

        val fileSpec = FileSpec.builder("com.example.generated", "GeneratedNavTest")
            .addFunction(funSpecBuilder.build())
            .build()

        try {
            val dependencies = Dependencies(false, *symbols.mapNotNull { it.containingFile }.toTypedArray())
            fileSpec.writeTo(codeGenerator, dependencies)
        } catch (e: Exception) {
            logger.error("Failed to generate file: ${e.message}")
        }

        isInvoked = true
        return emptyList()
    }
}
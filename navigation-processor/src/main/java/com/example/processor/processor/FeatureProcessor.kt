package com.example.processor.processor

import com.example.processor.RouteVisitor
import com.example.processor.util.Constant.FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH
import com.example.processor.util.Constant.FEATURE_PACKAGE_NAME
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class FeatureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var isInvoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isInvoked) return emptyList()

        val annotationName = FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH

        val symbols = resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        if (symbols.isEmpty()) return emptyList()

        // We instantiate the visitor once
        val visitor = RouteVisitor(codeGenerator, logger)

        // We iterate and ask every symbol to accept the visitor
        symbols.filter { it.validate() }
            .forEach { it.accept(visitor, Unit) }

        return symbols.filterNot { it.validate() }.toList()
    }
}
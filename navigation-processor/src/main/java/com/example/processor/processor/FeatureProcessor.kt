package com.example.processor.processor

import com.example.processor.RouteVisitor
import com.example.processor.generator.generateRouteGraph
import com.example.processor.model.RouteData
import com.example.processor.util.AnnotationExtension.getGraph
import com.example.processor.util.AnnotationExtension.getRouteParams
import com.example.processor.util.Constant.FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH
import com.example.processor.util.Constant.FEATURE_ROUTE_ANNOTATION
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class FeatureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private val routeFiles = mutableListOf<FileSpec>()
    lateinit var startDestination: TypeName
    lateinit var graphClass: TypeName

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotationName = FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH

        resolver
            .getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach { function ->
                logger.info("generating ${function.simpleName}", function)

                val annotation =
                    function.annotations.first { it.shortName.asString() == FEATURE_ROUTE_ANNOTATION }
                val annotationParams = RouteData(
                    routeParams = annotation.getRouteParams(),
                    graphClass = annotation.getGraph()
                )

                val isGraphClassDeclared =
                    annotationParams.graphClass.declaration.qualifiedName?.asString() in setOf(
                        "kotlin.Nothing",
                        "java.lang.Void"
                    )
                val isStartDestinationDuplicate =
                    ::startDestination.isInitialized && ::graphClass.isInitialized

                if (isGraphClassDeclared.not()) {
                    if (isStartDestinationDuplicate.not()) {
                        startDestination = annotationParams.routeParams.toTypeName()
                        graphClass = annotationParams.graphClass.toTypeName()
                    } else error("Multiple start destination!, check @FeatureRoute")
                }

                routeFiles.add(function.accept(RouteVisitor(), annotationParams))
            }

        return emptyList()
    }

    override fun finish() {
        super.finish()

        if (routeFiles.isNotEmpty()) {
            if (::startDestination.isInitialized.not() || ::graphClass.isInitialized.not()) {
                error("Start destination not declared")
            }

            routeFiles.forEach { fileSpec -> fileSpec.writeTo(codeGenerator, false) }

            generateRouteGraph(
                graphClassName = graphClass,
                startDestinationRouteParamsName = startDestination,
                routeFiles = routeFiles,
                packageName = "com.example.feature.graph",
                codeGenerator = codeGenerator
            )
        }
    }
}
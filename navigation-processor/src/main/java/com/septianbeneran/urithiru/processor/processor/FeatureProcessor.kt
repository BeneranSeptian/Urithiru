package com.septianbeneran.urithiru.processor.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.septianbeneran.urithiru.processor.RouteVisitor
import com.septianbeneran.urithiru.processor.generator.generateRouteGraph
import com.septianbeneran.urithiru.processor.model.RouteData
import com.septianbeneran.urithiru.processor.util.AnnotationExtension.getGraph
import com.septianbeneran.urithiru.processor.util.AnnotationExtension.getRouteParams
import com.septianbeneran.urithiru.processor.util.Constant.FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH
import com.septianbeneran.urithiru.processor.util.Constant.FEATURE_ROUTE_ANNOTATION
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_NAME
import com.septianbeneran.urithiru.processor.util.Constant.NAVIGATOR_CLASS_PACKAGE
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class FeatureProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private val routeFiles = mutableListOf<FileSpec>()
    private var startDestination: TypeName? = null
    private var graphClass: TypeName? = null
    private var startDestinationFunction: KSFunctionDeclaration? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotationName = FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH

        resolver
            .getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach { function ->
                logger.info("generating ${function.simpleName.asString()}", function)

                if (!function.hasNavigatorOnlyParameter()) {
                    logger.error(
                        "@FeatureRoute function ${function.simpleName.asString()} must take exactly one parameter: " +
                            "(navigator: $NAVIGATOR_CLASS_NAME)",
                        function
                    )
                    return@forEach
                }

                val annotation =
                    function.annotations.first { it.shortName.asString() == FEATURE_ROUTE_ANNOTATION }
                val routeParams = annotation.getRouteParams()
                if (routeParams == null) {
                    logger.error("@FeatureRoute is missing routeParams", function)
                    return@forEach
                }
                val graph = annotation.getGraph()
                val annotationParams = RouteData(routeParams = routeParams, graphClass = graph)

                val isStartDestination =
                    graph != null && graph.declaration.qualifiedName?.asString() !in NO_GRAPH_CLASSES

                if (isStartDestination) {
                    val existing = startDestinationFunction
                    if (existing != null) {
                        logger.error(
                            "Only one @FeatureRoute per feature module can set graph. " +
                                "${existing.simpleName.asString()} already sets it.",
                            function
                        )
                        return@forEach
                    }
                    startDestinationFunction = function
                    startDestination = routeParams.toTypeName()
                    graphClass = graph.toTypeName()
                }

                routeFiles.add(function.accept(RouteVisitor(), annotationParams))
            }

        return emptyList()
    }

    override fun finish() {
        super.finish()

        if (routeFiles.isNotEmpty()) {
            val graph = graphClass
            val start = startDestination
            if (graph == null || start == null) {
                logger.error(
                    "No start destination: exactly one @FeatureRoute in this feature module must set graph = <FeatureXNavGraph>::class"
                )
                return
            }

            routeFiles.forEach { fileSpec -> fileSpec.writeTo(codeGenerator, false) }

            generateRouteGraph(
                graphClassName = graph,
                startDestinationRouteParamsName = start,
                routeFiles = routeFiles,
                packageName = "com.septianbeneran.urithiru.feature.graph",
                codeGenerator = codeGenerator
            )
        }
    }

    private fun KSFunctionDeclaration.hasNavigatorOnlyParameter(): Boolean {
        val parameter = parameters.singleOrNull() ?: return false
        val type = parameter.type.resolve().declaration.qualifiedName?.asString()
        return type == "$NAVIGATOR_CLASS_PACKAGE.$NAVIGATOR_CLASS_NAME"
    }

    private companion object {
        val NO_GRAPH_CLASSES = setOf("kotlin.Nothing", "java.lang.Void")
    }
}
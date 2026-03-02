package com.example.processor.processor

import com.example.processor.generator.generateGraphRegistry
import com.example.processor.util.Constant.FEATURE_GRAPH_PACKAGE_NAME
import com.example.processor.util.Constant.GENERATED_GRAPH_PACKAGE_NAME
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class GraphProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    var featureGraphList = emptyList<KSFunctionDeclaration>()

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        featureGraphList = resolver.getDeclarationsFromPackage(FEATURE_GRAPH_PACKAGE_NAME)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        return emptyList()
    }

    override fun finish() {
        super.finish()

        generateGraphRegistry(
            graphFunctions = featureGraphList,
            packageName = GENERATED_GRAPH_PACKAGE_NAME,
            codeGenerator = codeGenerator
        )
    }
}

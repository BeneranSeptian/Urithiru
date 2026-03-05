package com.septianbeneran.urithiru.processor

import com.septianbeneran.urithiru.processor.processor.FeatureProcessor
import com.septianbeneran.urithiru.processor.processor.GraphProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val isAppModule = environment.options["isAppModule"] == "true"

        return if (isAppModule) {
            GraphProcessor(
                codeGenerator = environment.codeGenerator
            )
        } else {
            FeatureProcessor(
                codeGenerator = environment.codeGenerator,
                logger = environment.logger
            )
        }
    }
}
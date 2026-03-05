package com.septianbeneran.urithiru.processor.util

object Constant {
    const val ANNOTATION_PACKAGE_NAME = "com.septianbeneran.urithiru.core.navigation.annotation."

    const val FEATURE_PACKAGE_NAME = "com.septianbeneran.urithiru.feature."
    const val FEATURE_GRAPH_PACKAGE_NAME = "${FEATURE_PACKAGE_NAME}graph"

    const val APP_PACKAGE_NAME = "com.septianbeneran.urithiru."
    const val GENERATED_GRAPH_PACKAGE_NAME = "${APP_PACKAGE_NAME}graph"

    const val FEATURE_ROUTE_ANNOTATION = "FeatureRoute"
    const val FEATURE_GRAPH_ANNOTATION = "FeatureGraph"
    const val FEATUREROUTE_ANNOTATION_PACKAGE_FULL_PATH = "$ANNOTATION_PACKAGE_NAME$FEATURE_ROUTE_ANNOTATION"

    const val NAVIGATOR_CLASS_NAME = "Navigator"
    const val NAVIGATOR_CLASS_PACKAGE = "com.septianbeneran.urithiru.core.navigation.util"

}
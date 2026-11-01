package com.septianbeneran.urithiru.core.navigation.routeparams.feature_explore

import kotlinx.serialization.Serializable

/** [kind] is a `CompendiumKind` name, e.g. "Bosses". */
@Serializable
data class CompendiumDetailRoute(
    val kind: String,
    val id: String
)

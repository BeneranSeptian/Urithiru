package com.septianbeneran.urithiru.api.eldenring.data.remote.dto

import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry

/** A DTO of any compendium section, so the data source and repository can treat every section the same way. */
interface CompendiumResponse {
    fun mapToEntry(): CompendiumEntry
}

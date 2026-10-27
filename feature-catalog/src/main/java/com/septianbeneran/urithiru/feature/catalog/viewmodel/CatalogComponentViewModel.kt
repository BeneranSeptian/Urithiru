package com.septianbeneran.urithiru.feature.catalog.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.septianbeneran.urithiru.core.navigation.routeparams.feature_catalog.CatalogComponentRoute
import com.septianbeneran.urithiru.core.ui.base.BaseViewModel
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogEntries
import com.septianbeneran.urithiru.feature.catalog.catalog.CatalogEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogComponentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val entry: CatalogEntry? = CatalogEntries.find(savedStateHandle.toRoute<CatalogComponentRoute>().id)
}

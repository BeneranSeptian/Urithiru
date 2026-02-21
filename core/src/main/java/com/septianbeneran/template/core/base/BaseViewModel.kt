package com.septianbeneran.template.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core.remote.entity.ErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    private var _isCentralLoading = MutableStateFlow(false)
    var isCentralLoading = _isCentralLoading.asStateFlow()

    open fun <T : Any> collectApi(
        flow: Flow<ApiResult<T>>,
        isCentralLoading: Boolean? = null,
        onError: ((ErrorResponse) -> Unit)? = null,
        onLoading: (() -> Unit)? = null,
        onSuccess: ((T?) -> Unit)? = null,
    ) {

        viewModelScope.launch{
            flow.distinctUntilChanged().collect {
                when (it) {
                    is ApiResult.Error -> {
                        if(isCentralLoading != null) _isCentralLoading.value = false
                        onError?.invoke(it.error)
                    }
                    is ApiResult.Loading -> {
                        if(isCentralLoading != null) _isCentralLoading.value = isCentralLoading
                        onLoading?.invoke()
                    }
                    is ApiResult.Success -> {
                        if(isCentralLoading != null) _isCentralLoading.value = false
                        onSuccess?.invoke(it.data)
                    }
                }
            }
        }
    }
}
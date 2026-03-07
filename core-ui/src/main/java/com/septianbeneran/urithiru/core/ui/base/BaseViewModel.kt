package com.septianbeneran.urithiru.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
import com.septianbeneran.urithiru.core.remote.entity.ErrorResponse
import com.septianbeneran.urithiru.core.util.permission.PermissionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var permissionHandler: PermissionHandler
    private var _baseScreenUiState = MutableStateFlow(BaseScreenUiState())
    var baseScreenUiState = _baseScreenUiState.asStateFlow()

    private val _nonce = Channel<BaseNonce>(BUFFERED)
    val nonce = _nonce.receiveAsFlow()

    open fun <T : Any> collectApi(
        flow: Flow<ApiResult<T>>,
        isCentralLoading: Boolean? = null,
        onError: ((ErrorResponse) -> Unit)? = null,
        onLoading: (() -> Unit)? = null,
        onSuccess: ((T?) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            flow.distinctUntilChanged().collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        if(isCentralLoading != null) _baseScreenUiState.update {it.copy(showCentralLoading = false)}
                        onError?.invoke(result.error)
                    }
                    is ApiResult.Loading -> {
                        if(isCentralLoading != null) _baseScreenUiState.update {it.copy(showCentralLoading = isCentralLoading)}
                        onLoading?.invoke()
                    }
                    is ApiResult.Success -> {
                        if(isCentralLoading != null) _baseScreenUiState.update {it.copy(showCentralLoading = false)}
                        onSuccess?.invoke(result.data)
                    }
                }
            }
        }
    }

    fun sendNonce(nonce: BaseNonce) = viewModelScope.launch { _nonce.send(nonce) }

    open fun <T: Any> collectLocalData(
        flow: Flow<T>,
        updateState: (T) -> Unit
    ) {
        viewModelScope.launch {
            flow.distinctUntilChanged().collect {
                updateState(it)
            }
        }
    }
}
package com.septianbeneran.urithiru.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Dispatchers.Unconfined

interface CoroutineDispatcherProvider {
    fun main(): CoroutineDispatcher = Main
    fun default(): CoroutineDispatcher = Default
    fun io(): CoroutineDispatcher = IO
    fun unconfined(): CoroutineDispatcher = Unconfined
}
package com.septianbeneran.urithiru.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.septianbeneran.urithiru.core.BuildConfig
import com.septianbeneran.urithiru.core.BuildConfig.DUMMY_JSON_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.ELDEN_RING_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.JSON_BIN_BASE_URL
import com.septianbeneran.urithiru.core.annotation.DummyJsonNetwork
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
import com.septianbeneran.urithiru.core.annotation.JsonBinNetwork
import com.septianbeneran.urithiru.core.remote.interceptor.LogcatInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) addInterceptor(LogcatInterceptor())
            }
            .addNetworkInterceptor(ChuckerInterceptor.Builder(context).build())
            .build()
    }

    @EldenRingNetwork
    @Provides
    @Singleton
    fun provideEldenRingRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ELDEN_RING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @DummyJsonNetwork
    @Provides
    @Singleton
    fun provideDummyJsonRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DUMMY_JSON_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @JsonBinNetwork
    @Provides
    @Singleton
    fun provideJsonBinRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JSON_BIN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
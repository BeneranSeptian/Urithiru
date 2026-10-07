package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.BuildConfig.ELDEN_RING_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.IGDB_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.JSON_BIN_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.TWITCH_BASE_URL
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
import com.septianbeneran.urithiru.core.annotation.IgdbNetwork
import com.septianbeneran.urithiru.core.annotation.JsonBinNetwork
import com.septianbeneran.urithiru.core.annotation.TwitchNetwork
import com.septianbeneran.urithiru.core.remote.interceptor.LogcatInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(LogcatInterceptor())
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

    @TwitchNetwork
    @Provides
    @Singleton
    fun provideTwitchRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TWITCH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @IgdbNetwork
    @Provides
    @Singleton
    fun provideIgdbRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(IGDB_BASE_URL)
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
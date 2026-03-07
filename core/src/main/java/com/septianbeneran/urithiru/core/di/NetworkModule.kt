package com.septianbeneran.urithiru.core.di

import com.septianbeneran.urithiru.core.BuildConfig.ELDEN_RING_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.IGDB_BASE_URL
import com.septianbeneran.urithiru.core.BuildConfig.TWITCH_BASE_URL
import com.septianbeneran.urithiru.core.annotation.EldenRingNetwork
import com.septianbeneran.urithiru.core.annotation.IgdbNetwork
import com.septianbeneran.urithiru.core.annotation.TwitchNetwork
import com.septianbeneran.urithiru.core.remote.interceptor.LogcatInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

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
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ELDEN_RING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @TwitchNetwork
    @Provides
    @Singleton
    fun provideTwitchRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TWITCH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @IgdbNetwork
    @Provides
    @Singleton
    fun provideIgdbRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(IGDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
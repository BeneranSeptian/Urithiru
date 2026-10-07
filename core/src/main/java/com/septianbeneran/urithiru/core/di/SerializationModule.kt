package com.septianbeneran.urithiru.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {

    /**
     * Single JSON configuration shared by Retrofit and BaseDataStore.
     * - ignoreUnknownKeys: APIs may add fields we don't model (e.g. Elden Ring's `total`).
     * - explicitNulls = false: a missing nullable field decodes as null, and nulls are omitted when encoding.
     * A missing non-null field without a default still fails loudly instead of becoming a hidden null.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}

package dev.septianbeneran.urithiru.api.twitch.di

import com.septianbeneran.urithiru.core.annotation.TwitchNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.septianbeneran.urithiru.api.twitch.data.api.TwitchApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiTwitchModule {

    @Provides
    @Singleton
    fun provideTwitchApi(
        @TwitchNetwork retrofit: Retrofit
    ): TwitchApi {
        return retrofit.create(TwitchApi::class.java)
    }
}
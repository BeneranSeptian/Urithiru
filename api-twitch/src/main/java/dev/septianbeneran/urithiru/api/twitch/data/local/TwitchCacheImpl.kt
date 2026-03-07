package dev.septianbeneran.urithiru.api.twitch.data.local

import com.septianbeneran.urithiru.core.entity.twitch.TwitchOAuthToken
import com.septianbeneran.urithiru.core.util.datastore.BaseDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TwitchCacheImpl @Inject constructor(
    private val dataStore: BaseDataStore
): TwitchCache {

    override suspend fun saveTwitchOAuthToken(oAuthToken: TwitchOAuthToken) {
        dataStore.saveObject("OAUTH_TOKEN", oAuthToken)
    }

    override suspend fun loadTwitchOAuthToken(): Flow<TwitchOAuthToken?> = with(dataStore) {
        readObject<TwitchOAuthToken>("OAUTH_TOKEN")
    }
}
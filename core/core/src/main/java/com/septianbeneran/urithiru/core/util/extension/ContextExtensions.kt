package com.septianbeneran.urithiru.core.util.extension

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** The app's single Preferences DataStore. Use it through `BaseDataStore`. */
internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "base_data_store")

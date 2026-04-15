package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.App
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.network.RetrofitClient
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.StorageClient
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.settings.data.storage.SettingsStorage
import com.practicum.playlistmaker.settings.data.impl.SettingsStorageImpl
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.data.storage.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val baseUrl = "https://itunes.apple.com/"
private const val trackSearchKey = "TRACKS_SEARCH"
private const val historyKey = "HISTORY"
private const val playListMakerKey = "playlistmaker"

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(
                trackSearchKey,
                Context.MODE_PRIVATE
            )
    }

    factory { (androidContext() as App).gson }

    single<StorageClient<ArrayList<Track>>> {
        PrefsStorageClient(
            historyKey,
            object : TypeToken<ArrayList<Track>>() {}.type,
            get(),
            get()
        )
    }

    single<NetworkClient> {
        RetrofitClient(get(),androidContext())
    }

    single<SettingsStorage> {
        SettingsStorageImpl(
            androidContext().getSharedPreferences(
                playListMakerKey,
                Context.MODE_PRIVATE
            )
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
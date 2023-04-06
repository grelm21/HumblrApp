package com.example.humblr.koin

import android.app.Application
import android.content.SharedPreferences
import com.example.humblr.api.HeaderInterceptor
import com.example.humblr.api.IRedditApi
import com.example.humblr.api.IRedditApi.Companion.BASE_URL
import com.example.humblr.data.SharedPreference
import com.example.humblr.data.SharedPreference.SharedPreference.Constants.PREFERENCE_KEY
import com.example.humblr.repository.*
import com.example.humblr.viewmodels.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val jsonModule = module {
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }
}

val networkModule = module {

    single {
        OkHttpClient.Builder().addInterceptor(HeaderInterceptor(get())).build()
    }

    factory {
        Retrofit.Builder().client(get())//добавляет интерсептор хедера для авторизации
            .addConverterFactory(MoshiConverterFactory.create(get())).baseUrl(BASE_URL).build()
    }

    single {
        get<Retrofit>().create(IRedditApi::class.java)
    }
}

val repoModule = module {
    single { RepositorySharedPreferences(get()) }
    single { FeedRepository(get()) }
    single { PostRepository(get()) }
    single { ProfileRepository(get()) }
    single { FavoritesRepository(get()) }
}

val preferencesModule = module {

    single {
        getSharedPrefs(androidApplication())
    }

    single { SharedPreference.SharedPreference(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { PostViewModel(get()) }
    viewModel { SubredditViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
}

fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences(
        PREFERENCE_KEY, android.content.Context.MODE_PRIVATE
    )
}
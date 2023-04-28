package com.example.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.storyapp.data.AuthRepository
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.local.room.StoryDatabase

object Injection {

    fun provideAuthRepository(dataStore: DataStore<Preferences>): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(dataStore)
        return AuthRepository.getInstance(apiService, authPreferences)
    }

    fun provideStoryRepository(context: Context, dataStore: DataStore<Preferences>): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(dataStore)
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return StoryRepository.getInstance(apiService, authPreferences, dao)
    }
}
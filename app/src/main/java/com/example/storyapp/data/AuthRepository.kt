package com.example.storyapp.data

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.data.local.AuthPreferences
import kotlinx.coroutines.flow.*

class AuthRepository private constructor(private val apiService: ApiService, private val authPreferences: AuthPreferences){

    fun isLogin(): Flow<String?> = flow {emitAll(authPreferences.getToken())}

    fun login(email: String, password: String): Flow<StoryResult<String>> = flow {
        emit(StoryResult.Loading)
        try {
            val response = apiService.login(email, password)
            val token = response.loginResult.token
            authPreferences.saveToken(token)
            emit(StoryResult.Success(response.message))
        }catch (e: Exception){
            Log.d("AuthRepository", "login: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String): Flow<StoryResult<String>> = flow {
        emit(StoryResult.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(StoryResult.Success(response.message))
        }catch (e: Exception){
            Log.d("AuthRepository", "register: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
        }
    }

    fun logout(): Flow<StoryResult<String>> = flow {
        emit(StoryResult.Loading)
        authPreferences.logout()
        emit(StoryResult.Success("success"))
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreferences: AuthPreferences
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, authPreferences)
            }.also { instance = it }
    }
}
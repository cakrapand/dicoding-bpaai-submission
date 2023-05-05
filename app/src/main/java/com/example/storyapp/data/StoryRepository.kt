package com.example.storyapp.data

import android.util.Log
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.local.room.StoryDao
import com.example.storyapp.utils.reduceFileImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(private val apiService: ApiService, private val authPreferences: AuthPreferences, private val storyDao: StoryDao){

    fun getAllStories(): Flow<StoryResult<List<Story>>> = flow {
        emit(StoryResult.Loading)
        try {
            authPreferences.getToken().collect{
                if(it != null){
                    val response = apiService.getAllStories(it)
                    storyDao.clearStory()
                    for (story in response.listStory) {
                        storyDao.addStory(story)
                    }
                    emit(StoryResult.Success(response.listStory))
                }
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
        }
    }

    fun getDetailStory(id: String): Flow<StoryResult<Story>> = flow {
        emit(StoryResult.Loading)
        try {
            authPreferences.getToken().collect{
                if(it != null){
                    val response = apiService.getDetailStory(it, id)
                    emit(StoryResult.Success(response.story))
                }
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
        }
    }

    fun addStory(description: String, file: File): Flow<StoryResult<String>> = flow{
        emit(StoryResult.Loading)
        val reducedFile = reduceFileImage(file)
        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        try {
            authPreferences.getToken().collect{
                if(it != null){
                    val response = apiService.addStory(it, imageMultipart, desc)
                    emit(StoryResult.Success(response.message))
                }
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreferences: AuthPreferences,
            storyDao: StoryDao,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreferences, storyDao)
            }.also { instance = it }
    }
}
package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.utils.reduceFileImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(private val apiService: ApiService, private val authPreferences: AuthPreferences, private val storyDatabase: StoryDatabase){

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<StoryEntity>> = liveData {
        EspressoIdlingResource.increment()
        authPreferences.getToken().collect{
            if(it != null){
                val response = Pager(
                    config = PagingConfig(pageSize = 5),
                    remoteMediator = StoryRemoteMediator(storyDatabase, apiService, it),
                    pagingSourceFactory = {  storyDatabase.storyDao().getStories() },
                ).liveData
                emitSource(response)
                EspressoIdlingResource.decrement()
            }
        }

    }

    fun getAllStoriesWithLoc(): Flow<StoryResult<List<Story>>> = flow {
        emit(StoryResult.Loading)
        EspressoIdlingResource.increment()
        try {
            authPreferences.getToken().collect {
                if (it != null) {
                    val response = apiService.getAllStoriesWithLoc(it)
                    emit(StoryResult.Success(response.listStory))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStoriesWithLoc: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun getDetailStory(id: String): Flow<StoryResult<Story>> = flow {
        emit(StoryResult.Loading)
        EspressoIdlingResource.increment()
        try {
            authPreferences.getToken().collect {
                if (it != null) {
                    val response = apiService.getDetailStory(it, id)
                    emit(StoryResult.Success(response.story))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }

    fun addStory(description: String, file: File, lat: Double?, lon:Double?): Flow<StoryResult<String>> = flow{
        emit(StoryResult.Loading)
        EspressoIdlingResource.increment()
        val reducedFile = reduceFileImage(file)
        val desc = description.toRequestBody("text/plain".toMediaType())
        val latitude = if (lat != null) lat.toString().toRequestBody("text/plain".toMediaType()) else null
        val longitude = if (lon != null) lon.toString().toRequestBody("text/plain".toMediaType()) else null
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        try {
            authPreferences.getToken().collect {
                if (it != null) {
                    val response = apiService.addStory(it, imageMultipart, desc, latitude, longitude)
                    emit(StoryResult.Success(response.message))
                    EspressoIdlingResource.decrement()
                }
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
            emit(StoryResult.Error(e.message.toString()))
            EspressoIdlingResource.decrement()
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreferences: AuthPreferences,
            storyDatabase: StoryDatabase,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreferences, storyDatabase)
            }.also { instance = it }
    }
}
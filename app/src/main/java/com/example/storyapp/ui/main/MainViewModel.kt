package com.example.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.data.AuthRepository
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {

    private val _listStory = MutableLiveData<Result<List<Story>>>()
    val listStory : LiveData<Result<List<Story>>>
        get() = _listStory

    init {
        getAllStories()
    }

    fun getAllStories(){
        viewModelScope.launch {
            storyRepository.getAllStories().collect{
                _listStory.value = it
            }
        }
    }

    fun isLogin() = authRepository.isLogin().asLiveData()

    fun logout() = authRepository.logout().asLiveData()
}
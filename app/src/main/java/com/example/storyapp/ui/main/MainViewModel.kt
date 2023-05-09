package com.example.storyapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.AuthRepository
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.entity.StoryEntity

class MainViewModel(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {

    private val _listStory = MutableLiveData<PagingData<StoryEntity>>()
    val listStory : LiveData<PagingData<StoryEntity>>
        get() = _listStory

    private val observer  = Observer<PagingData<StoryEntity>>{ _listStory.value = it }

    init {
        getAllStories()
    }

    fun getAllStories(){
        storyRepository.getAllStories().cachedIn(viewModelScope).observeForever(observer)
    }

    fun isLogin() = authRepository.isLogin().asLiveData()

    fun logout() = authRepository.logout().asLiveData()

    override fun onCleared() {
        storyRepository.getAllStories().removeObserver(observer)
        super.onCleared()
    }
}
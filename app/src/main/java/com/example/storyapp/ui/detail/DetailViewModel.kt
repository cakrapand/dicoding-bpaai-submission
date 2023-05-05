package com.example.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryResult
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<StoryResult<Story>>()
    val story : LiveData<StoryResult<Story>>
        get() = _story

    fun getDetailStory(id: String){
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect{
                _story.value = it
            }
        }
    }

}
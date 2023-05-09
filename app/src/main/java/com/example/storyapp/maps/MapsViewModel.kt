package com.example.storyapp.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.StoryResult
import com.example.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<StoryResult<List<Story>>>()
    val story : LiveData<StoryResult<List<Story>>>
        get() = _story

    init{
        getAllStoriesWithLoc()
    }

    private fun getAllStoriesWithLoc(){
        viewModelScope.launch {
            storyRepository.getAllStoriesWithLoc().collect{
                _story.value = it
            }
        }
    }

}
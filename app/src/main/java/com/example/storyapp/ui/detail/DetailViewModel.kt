package com.example.storyapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.AuthRepository
import com.example.storyapp.data.Result
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.Story
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Result<Story>>()
    val story : LiveData<Result<Story>>
        get() = _story

    fun getDetailStory(id: String){
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect{
                _story.value = it
            }
        }
    }

}
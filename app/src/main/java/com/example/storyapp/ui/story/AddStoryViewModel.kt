package com.example.storyapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun addStory(description: String, file: File) = storyRepository.addStory(description, file).asLiveData()
}
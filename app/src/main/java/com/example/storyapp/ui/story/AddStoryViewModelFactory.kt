package com.example.storyapp.ui.story

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.di.Injection

class AddStoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AddStoryViewModelFactory? = null
        fun getInstance(context: Context, dataStore: DataStore<Preferences>): AddStoryViewModelFactory = instance ?: synchronized(this) {
            instance ?: AddStoryViewModelFactory(Injection.provideStoryRepository(context, dataStore))
        }.also { instance = it }
    }
}
package com.example.storyapp.ui.detail

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.di.Injection

class DetailViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DetailViewModelFactory? = null
        fun getInstance(context: Context, dataStore: DataStore<Preferences>): DetailViewModelFactory = instance ?: synchronized(this) {
            instance ?: DetailViewModelFactory(Injection.provideStoryRepository(context, dataStore))
        }.also { instance = it }
    }
}
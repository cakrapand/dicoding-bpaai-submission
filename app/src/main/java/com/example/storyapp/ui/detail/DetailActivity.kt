package com.example.storyapp.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.main.MainViewModel
import com.example.storyapp.ui.main.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val detailViewModel: DetailViewModel by viewModels {
        DetailViewModelFactory.getInstance(application, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)

        if(id != null && detailViewModel.story.value == null) {
            detailViewModel.getDetailStory(id)
        }

        detailViewModel.story.observe(this){ result ->
            when(result){
                is Result.Loading ->{
                    binding.progressBarDetail.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBarDetail.visibility = View.GONE
                    Glide.with(this)
                        .load(result.data.photoUrl)
                        .into(binding.ivDetailPhoto)
                    binding.tvDetailName.text = result.data.name
                    binding.tvDetailDescription.text = result.data.description
                }
                is Result.Error ->{
                    binding.progressBarDetail.visibility = View.GONE
                    Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityDetailBinding = null
    }

    companion object{
        const val EXTRA_ID = "extra_id"
    }
}
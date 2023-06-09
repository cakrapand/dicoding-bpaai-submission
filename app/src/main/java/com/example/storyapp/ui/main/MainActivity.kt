package com.example.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.StoryResult
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.maps.MapsActivity
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.story.AddStoryActivity

class MainActivity : AppCompatActivity() {

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherAddStoryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mainViewModel.getAllStories()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyAdapter = StoryAdapter { story, optionsCompat ->
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, story.id)
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.rvListUser.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter =
                storyAdapter.withLoadStateFooter(footer = LoadingStateAdapter { storyAdapter.retry() })
        }

        mainViewModel.listStory.observe(this) {
            storyAdapter.submitData(lifecycle, it)
            if (it == null) Toast.makeText(
                this@MainActivity,
                getString(R.string.data_empty),
                Toast.LENGTH_SHORT
            ).show()
        }

        mainViewModel.isLogin().observe(this) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.logged_out),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }

        binding.btnAddStory.setOnClickListener {
            launcherAddStoryActivity.launch(Intent(this, AddStoryActivity::class.java))
        }

        binding.btnMaps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                mainViewModel.logout().observe(this) {
                    when (it) {
                        is StoryResult.Loading -> {
                            binding.progrerssBarMain.visibility = View.VISIBLE
                        }
                        is StoryResult.Success -> {
                            binding.progrerssBarMain.visibility = View.GONE
                        }
                        is StoryResult.Error -> {
                            binding.progrerssBarMain.visibility = View.GONE
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.logout_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                true
            }
            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }

    companion object {
        const val RESULT_OK = 110
    }
}
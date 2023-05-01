package com.example.storyapp.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.story.AddStoryActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private var _activityMainBinding : ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherAddStoryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            mainViewModel.getAllStories()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.listStory.observe(this){ result ->
            when(result){
                is Result.Loading ->{
                    binding.progrerssBarMain.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progrerssBarMain.visibility = View.GONE
                    binding.rvListUser.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter = StoryAdapter(result.data) { story, optionsCompat ->
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                            startActivity(intent, optionsCompat.toBundle())
                        }
                    }
                }
                is Result.Error ->{
                    binding.progrerssBarMain.visibility = View.GONE
                    Toast.makeText(this@MainActivity, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        mainViewModel.isLogin().observe(this){
            if(it.isNullOrEmpty()){
                Toast.makeText(this@MainActivity, getString(R.string.logged_out), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }

        binding.btnAddStory.setOnClickListener {
            launcherAddStoryActivity.launch(Intent(this, AddStoryActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
//                mainViewModel.logout()
                mainViewModel.logout().observe(this){
                    when(it){
                        is Result.Loading -> {
                            binding.progrerssBarMain.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progrerssBarMain.visibility = View.GONE
                        }
                        is Result.Error -> {
                            binding.progrerssBarMain.visibility = View.GONE
                            Toast.makeText(this@MainActivity, getString(R.string.logout_error), Toast.LENGTH_SHORT).show()
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

    companion object{
        const val RESULT_OK = 110
    }
}
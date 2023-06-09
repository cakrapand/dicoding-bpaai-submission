package com.example.storyapp.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.data.StoryResult
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.camera.CameraActivity
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.utils.rotateFile
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private var getFile: File? = null

    private var _activityAddStoryBinding : ActivityAddStoryBinding? = null
    private val binding get() = _activityAddStoryBinding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        AddStoryViewModelFactory.getInstance(application, dataStore)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            startCameraX()
        }else{
            Toast.makeText(this, getString(R.string.allow_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {
                Toast.makeText(this, getString(R.string.allow_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupAction(){
        binding.btnCamera.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA))  startCameraX()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.cbUploadLocation.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                getMyLastLocation()
            }else{
                location = null
            }
        }

        binding.btnAdd.setOnClickListener {
            uploadImage()
        }
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED


    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        val description = binding.edAddDescription.text.toString().trim()
        if (description.isNotBlank() && getFile != null) {
//            EspressoIdlingResource.increment()
            val file = getFile as File

            addStoryViewModel.addStory(description, file, location?.latitude, location?.longitude).observe(this){
                when(it){
                    is StoryResult.Loading -> {
                        binding.progrerssBarAdd.visibility = View.VISIBLE
                    }
                    is StoryResult.Success -> {
                        binding.progrerssBarAdd.visibility = View.GONE
                        setResult(MainActivity.RESULT_OK, Intent())
                        finish()
                    }
                    is StoryResult.Error -> {
                        binding.progrerssBarAdd.visibility = View.GONE
                        Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this@AddStoryActivity, getString(R.string.story_invalid), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _activityAddStoryBinding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}
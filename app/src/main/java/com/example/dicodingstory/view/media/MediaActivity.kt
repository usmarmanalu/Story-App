package com.example.dicodingstory.view.media

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstory.R
import com.example.dicodingstory.ViewModelFactory
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.databinding.ActivityMediaBinding
import com.example.dicodingstory.util.getImageUri
import com.example.dicodingstory.util.reduceFileImage
import com.example.dicodingstory.util.uriToFile
import com.example.dicodingstory.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var description: EditText
    private var currentImgUri: Uri? = null
    private lateinit var token: String
    private var includeLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<MediaViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        when {
            isGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                myLastLocation()
            }

            isGranted[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                myLastLocation()
            }

            else -> {
                binding.locationCheckbox.isChecked = false
            }
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION.toString()
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Upload Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(
                REQUIRED_PERMISSION
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStory() }

        description = findViewById(R.id.et_desc)
        description.setOnClickListener {
            description.isFocusable = true
            description.isFocusableInTouchMode = true
            description.requestFocus()
            showKeyboard(description)
        }

        binding.locationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (isLocationEnable()) {
                    enableLocationDialog()
                }
                lifecycleScope.launch {
                    myLastLocation()
                }
            } else {
                includeLocation = null
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImgUri = uri
            showImage()
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImgUri = getImageUri(this)
        launcherIntentCamera.launch(currentImgUri)
    }

    private fun showImage() {
        currentImgUri?.let {
            Log.d("Image URI", "showImage : $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadStory() {
        currentImgUri?.let { uri ->
            val imgFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.etDesc.text.toString()
            showLoading(true)

            viewModel.getSession().observe(this) { user ->
                token = user.token
            }

            viewModel.uploadStory(token, imgFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.warning))
    }

    private fun isLocationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableLocationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Permission Location")
            setMessage("Enable Location?")
            setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                binding.locationCheckbox.isChecked = false
            }
            create()
            show()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            includeLocation = locationResult.lastLocation
        }
    }

    private fun newLocation() {
        val location = LocationRequest()
        location.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        location.interval = TimeUnit.SECONDS.toMillis(1)
        location.fastestInterval = 0
        location.numUpdates = 1
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )

            Looper.myLooper()?.let {
                fusedLocationClient.requestLocationUpdates(
                    location, locationCallback, it
                )
            }
    }

    @SuppressLint("MissingPermission")
    private fun myLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    includeLocation = location
                } else {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
                    newLocation()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

        )
    }
}

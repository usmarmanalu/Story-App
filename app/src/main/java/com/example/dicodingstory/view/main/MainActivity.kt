package com.example.dicodingstory.view.main

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.R
import com.example.dicodingstory.ViewModelFactory
import com.example.dicodingstory.adapter.StoriesAdapter
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.view.media.MediaActivity
import com.example.dicodingstory.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storiesAdapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.rvStories
        recyclerView.layoutManager = LinearLayoutManager(this)
        storiesAdapter = StoriesAdapter()
        recyclerView.adapter = storiesAdapter

        setupView()

        setAppLocale("en", resources)
        setAppLocale("in", resources)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }

                R.id.setting -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                else -> false
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        viewModel.getSession().observe(this@MainActivity) { user ->
            if (user != null && user.isLogin) {
                viewModel.getAllStories(user.token)
            }
        }

        viewModel.storiesLiveData.observe(this) { resultState ->
            when (resultState) {
                is ResultState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is ResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val response = resultState.data.listStory
                    Toast.makeText(this@MainActivity, "Sukses Menampilkan Data", Toast.LENGTH_SHORT)
                        .show()
                    storiesAdapter.submitList(response)

                }

                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Gagal Memuat Data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { _, _ ->
            viewModel.logout()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Tidak") { _, _ -> }
        builder.show()
    }

    private fun setAppLocale(languageCode: String, resources: Resources) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}

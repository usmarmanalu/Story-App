package com.example.dicodingstory.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingstory.data.response.ListStoryItem
import com.example.dicodingstory.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyItem = intent.getParcelableExtra<ListStoryItem>("storyItem")
        storyItem?.let {
            binding.apply {
                tvName.text = it.name
                tvDesc.text = it.description
                Glide.with(binding.root)
                    .load(it.photoUrl)
                    .into(ivDetail)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
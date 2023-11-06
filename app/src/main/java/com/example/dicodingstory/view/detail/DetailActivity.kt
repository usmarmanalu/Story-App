package com.example.dicodingstory.view.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.data.response.ListStoryItem
import com.example.dicodingstory.databinding.ActivityDetailBinding
import com.example.dicodingstory.view.maps.MapsActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyItem = intent.getParcelableExtra<ListStoryItem>("storyItem")
        storyItem?.let {
            binding.apply {
                tvName.text = it.name

                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

                val outputFormat =
                    SimpleDateFormat("dd MMMM yyyy - HH:mm:ss a", Locale.getDefault())

                try {
                    val date = inputFormat.parse(it.createdAt)
                    val formattedDate = outputFormat.format(date)
                    val formatteAgo = getTimeAgo(date.time)
                    tvCreatedStory.text = formattedDate
                    tvCreatedAgo.text = formatteAgo
                } catch (e: ParseException) {
                    e.printStackTrace()
                    tvCreatedStory.text = "Format tanggal tidak valid"
                }

                tvDesc.text = it.description

                Glide.with(binding.root)
                    .load(it.photoUrl)
                    .into(ivDetail)

                if (it.lat != null && it.lon != null) {
                    tvLat.text = "${it.lat}" + ", "
                    tvLon.text = "${it.lon}"

                    setClickActionForLocationText(tvLat, it.lat, it.lon)
                    setClickActionForLocationText(tvLon, it.lat, it.lon)
                } else {
                    tvLat.text = ""
                    tvLon.text = ""
                }
            }
        }
    }

    private fun setClickActionForLocationText(textView: TextView, lat: Double, lon: Double) {
        val mapIntent = Intent(this, MapsActivity::class.java)
        mapIntent.putExtra("lat", lat)
        mapIntent.putExtra("lon", lon)

        textView.setOnClickListener {
            startActivity(mapIntent)
        }
    }

    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val timeDifference = now - time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds)
        val hours = TimeUnit.MINUTES.toHours(minutes)

        return when {
            seconds < 60 -> "baru saja"
            minutes < 60 -> "$minutes menit yang lalu"
            hours < 24 -> "Diposting $hours jam yang lalu"
            else -> {
                val sdf = SimpleDateFormat("dd MMMM yyyy - HH:mm:ss a", Locale.getDefault())
                sdf.format(Date(time))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

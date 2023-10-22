package com.example.dicodingstory.view.media

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.StoryRepository
import java.io.File

class MediaViewModel(private val repository: StoryRepository) : ViewModel() {

    fun uploadStory(file: File, description: String) = repository.uploadStory(file, description)
}
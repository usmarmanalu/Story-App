package com.example.dicodingstory.view.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.pref.UserModel
import java.io.File

class MediaViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadStory(token: String, file: File, description: String) =
        repository.uploadStory(token, file, description)
}
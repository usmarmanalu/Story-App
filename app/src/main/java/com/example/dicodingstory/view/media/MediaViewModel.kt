package com.example.dicodingstory.view.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.response.GetAllStoriesResponse
import java.io.File

class MediaViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesLiveData = MutableLiveData<ResultState<GetAllStoriesResponse>>()
    val storiesLiveData: LiveData<ResultState<GetAllStoriesResponse>> = _storiesLiveData
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadStory(token: String, file: File, description: String) =
        repository.uploadStory(token, file, description)
}
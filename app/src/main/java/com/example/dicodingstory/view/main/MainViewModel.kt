package com.example.dicodingstory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.response.GetAllStoriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesLiveData = MutableLiveData<ResultState<GetAllStoriesResponse>>()
    val storiesLiveData: LiveData<ResultState<GetAllStoriesResponse>> = _storiesLiveData

    fun getAllStories(token: String) {
        _storiesLiveData.value = ResultState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _storiesLiveData.postValue(ResultState.Loading)
            try {
                val response = repository.getStories(token)
                _storiesLiveData.postValue(response)
            } catch (e: Exception) {
                _storiesLiveData.postValue(ResultState.Error(e.message ?: "Terjadi Kesalahan"))
            }

        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}

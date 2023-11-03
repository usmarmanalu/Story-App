package com.example.dicodingstory.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.dicodingstory.data.MapsRepository
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.response.ListStoryItem

class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    private val _locationLiveData = MediatorLiveData<ResultState<List<ListStoryItem>>>()
    val locationLiveData: LiveData<ResultState<List<ListStoryItem>>> = _locationLiveData

    fun getStoriesWithLocation(token: String) {
        val response = repository.getLocationStories(token)
        _locationLiveData.addSource(response) { result ->
            _locationLiveData.value = result
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}




package com.example.dicodingstory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val getStory: LiveData<PagingData<ListStoryItem>> =
        repository.getStory(getToken()).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private fun getToken(): String {
        var tokenUser = ""
        viewModelScope.launch {
            repository.getSession().collect { token ->
                tokenUser = token.token
            }
        }
        return tokenUser
    }
}

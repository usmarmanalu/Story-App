package com.example.dicodingstory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.dicodingstory.data.api.ApiService
import com.example.dicodingstory.data.pref.UserPreference
import com.example.dicodingstory.data.response.ListStoryItem
import kotlinx.coroutines.Dispatchers

class MapsRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun getLocationStories(token: String): LiveData<ResultState<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(ResultState.Loading)
            try {
                val response = apiService.getStoriesWithLocation("Bearer $token")
                val data = response.listStory
                emit(ResultState.Success(data))
            } catch (e: Exception) {
                e.printStackTrace()
                ResultState.Error(e.message ?: "Terjadi Kesalahan")
            }
        }

    fun getSession() = userPreference.getSession()

    companion object {
        @Volatile
        private var instance: MapsRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): MapsRepository =
            instance ?: synchronized(this) {
                instance ?: MapsRepository(apiService, userPreference)
            }.also { instance = it }
    }
}

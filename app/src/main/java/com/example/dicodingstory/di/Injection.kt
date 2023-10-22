package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.api.ApiConfig
import com.example.dicodingstory.data.pref.UserPreference
import com.example.dicodingstory.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService, userPreference)
    }
}


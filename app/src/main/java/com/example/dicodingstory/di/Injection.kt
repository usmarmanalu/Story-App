package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.AuthRepository
import com.example.dicodingstory.data.MapsRepository
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.api.ApiConfig
import com.example.dicodingstory.data.pref.UserPreference
import com.example.dicodingstory.data.pref.dataStore

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstanceAuth(apiService, userPreference)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, userPreference)
    }

    fun provideMapsRepository(context: Context): MapsRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return MapsRepository.getInstance(apiService, userPreference)
    }

}

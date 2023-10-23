package com.example.dicodingstory.data

import androidx.lifecycle.liveData
import com.example.dicodingstory.data.api.ApiService
import com.example.dicodingstory.data.pref.UserPreference
import com.example.dicodingstory.data.response.FileUploadResponse
import com.example.dicodingstory.data.response.GetAllStoriesResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(token: String): ResultState<GetAllStoriesResponse> {
        return try {
            val response = apiService.getStories("Bearer $token")
            if (response.isSuccessful) {
                ResultState.Success(response.body() ?: GetAllStoriesResponse())
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Gagal mengambil data"
                ResultState.Error(errorMessage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResultState.Error(e.message ?: "Terjadi Kesalahan")
        }
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() = userPreference.logout()

    fun uploadStory(token: String, imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                apiService.uploadImage("Bearer $token", multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}

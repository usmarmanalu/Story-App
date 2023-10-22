package com.example.dicodingstory.data

import androidx.lifecycle.liveData
import com.example.dicodingstory.data.api.ApiService
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.pref.UserPreference
import com.example.dicodingstory.data.response.FileUploadResponse
import com.example.dicodingstory.data.response.GetAllStoriesResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
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

    suspend fun getTokenFromDataStore(): String? {
        val user = userPreference.getSession().first()
        return if (user.isLogin) user.token else null
    }

    suspend fun register(name: String, email: String, password: String): ResultState<Boolean> {
        return try {
            apiService.register(name, email, password).message
            ResultState.Success(true)
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Terjadi Kesalahan")
        }
    }

    suspend fun login(email: String, password: String): ResultState<UserModel> {
        return try {
            val loginResponse = apiService.login(email, password)
            if (loginResponse.loginResult != null) {
                val userModel = UserModel(
                    email = loginResponse.loginResult.name ?: "",
                    token = loginResponse.loginResult.token ?: "",
                    isLogin = true
                )
                userPreference.saveSession(userModel)
                ResultState.Success(userModel)
            } else {
                ResultState.Error("Login Gagal")
            }
        } catch (e: Exception) {
            return ResultState.Error(e.message ?: "Terjadi Kesalahan")
        }
    }

    suspend fun getStories(): ResultState<GetAllStoriesResponse> {
        return try {
            val response = apiService.getStories()
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

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    suspend fun logout() = userPreference.logout()

    fun uploadStory(imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
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

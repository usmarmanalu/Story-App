package com.example.dicodingstory.data

import com.example.dicodingstory.data.api.ApiService
import com.example.dicodingstory.data.pref.UserModel
import com.example.dicodingstory.data.pref.UserPreference

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

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

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstanceAuth(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, userPreference)
            }.also { instance = it }
    }
}
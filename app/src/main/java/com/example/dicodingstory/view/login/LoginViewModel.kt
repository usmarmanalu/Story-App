package com.example.dicodingstory.view.login

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.AuthRepository
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.data.pref.UserModel

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    suspend fun login(email: String, password: String): ResultState<UserModel> {
        return repository.login(email, password)
    }

    suspend fun saveSession(userModel: UserModel) {
        repository.saveSession(userModel)
    }
}

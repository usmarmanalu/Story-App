package com.example.dicodingstory.view.signup

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.data.AuthRepository
import com.example.dicodingstory.data.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String): ResultState<Boolean> {
        return withContext(Dispatchers.IO) {
            repository.register(name, email, password)
        }
    }
}

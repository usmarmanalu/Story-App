package com.example.dicodingstory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.data.AuthRepository
import com.example.dicodingstory.data.MapsRepository
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.view.login.LoginViewModel
import com.example.dicodingstory.view.main.MainViewModel
import com.example.dicodingstory.view.maps.MapsViewModel
import com.example.dicodingstory.view.media.MediaViewModel
import com.example.dicodingstory.view.signup.SignupViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repositoryAuth: AuthRepository,
    private val repositoryStory: StoryRepository,
    private val repositoryMaps: MapsRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repositoryAuth) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repositoryAuth) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repositoryStory) as T
            }

            modelClass.isAssignableFrom(MediaViewModel::class.java) -> {
                MediaViewModel(repositoryStory) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repositoryMaps) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideStoryRepository(context),
                    Injection.provideMapsRepository(context),
                )
            }.also { instance = it }
        }
    }
}

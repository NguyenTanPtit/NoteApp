package com.example.noteapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.repository.AuthRepository

class LoginWithGGViewModelFactory(private val repo:AuthRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginWithGGViewModel(repo) as T
    }
}
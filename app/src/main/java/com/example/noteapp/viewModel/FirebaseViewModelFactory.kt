package com.example.noteapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.repository.FirebaseRepository

@Suppress("UNCHECKED_CAST")
class FirebaseViewModelFactory(private val repo:FirebaseRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FirebaseViewModel(repo) as T
    }
}
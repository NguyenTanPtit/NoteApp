package com.example.noteapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noteapp.model.User
import com.example.noteapp.repository.AuthRepository
import com.example.noteapp.repository.ResponseState
import com.google.firebase.auth.AuthCredential

class LoginWithGGViewModel(private val repo: AuthRepository) :ViewModel(){
    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = repo.firebaseSignInWithGoogle(googleAuthCredential)
    }



}

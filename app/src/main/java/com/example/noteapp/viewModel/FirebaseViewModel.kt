package com.example.noteapp.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noteapp.model.User
import com.example.noteapp.repository.FirebaseRepository
import com.example.noteapp.repository.ResponseState
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirebaseViewModel(private val repo: FirebaseRepository) : ViewModel() {
    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = repo.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun syncNoteAndRemind(uid: String, context: Context, viewModel: NoteActivityViewModel) {
        GlobalScope.launch(Dispatchers.Main) {
            repo.syncNoteAndRemind(uid, context, viewModel)
            Toast.makeText(context, "Sync Done", Toast.LENGTH_SHORT).show()
        }
    }

}

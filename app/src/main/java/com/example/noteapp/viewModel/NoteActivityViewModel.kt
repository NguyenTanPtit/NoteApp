package com.example.noteapp.viewModel

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repo :NoteRepository) :ViewModel(){

    fun saveNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.addNote(note)
    }
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.updateNote(note)
    }
    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteNote(note)
    }
    fun searchNote(query: String) :LiveData<List<Note>> {
        return repo.searchNote(query)
    }
    fun getAllNote() : LiveData<List<Note>> = repo.getNote()

    fun saveReminder(reminder: Reminder,context:Context) = viewModelScope.launch {

        repo.addReminder(reminder)
    }

    private fun setNotifyReminder(reminder: Reminder,context: Context?){
        var isPushNotificationGranted = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.POST_NOTIFICATIONS)
        } == PackageManager.PERMISSION_GRANTED
        if(!isPushNotificationGranted){

        }
    }
}
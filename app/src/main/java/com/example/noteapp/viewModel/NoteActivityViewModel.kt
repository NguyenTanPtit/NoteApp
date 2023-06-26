package com.example.noteapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
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
}
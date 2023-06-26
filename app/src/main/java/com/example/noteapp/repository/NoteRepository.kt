package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.db.NoteDB
import com.example.noteapp.model.Note

class NoteRepository(private val db:NoteDB) {
    fun getNote() = db.getNoteDao().getAllNote()

    fun searchNote(query: String):LiveData<List<Note>> = db.getNoteDao().searchNote(query)

    suspend fun  addNote(note:Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note:Note)  =  db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note)  = db.getNoteDao().deleteNote(note)

}
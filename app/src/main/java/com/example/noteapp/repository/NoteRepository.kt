package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.db.NoteDB
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder

class NoteRepository(private val db:NoteDB) {

    //CRUD Note
    fun getNote() = db.getNoteDao().getAllNote()

    fun getNoteSync() = db.getNoteDao().getAllNoteSync()

    fun searchNote(query: String):LiveData<List<Note>> = db.getNoteDao().searchNote(query)

    suspend fun  addNote(note:Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note:Note)  =  db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note)  = db.getNoteDao().deleteNote(note)


    //CRUD Reminder
    fun getReminder() = db.getReminderDao().getAllReminder()

    fun getReminderSync()= db.getReminderDao().getAllReminderSync()

    fun searchReminder(query: String): LiveData<List<Reminder>>
            = db.getReminderDao().searchReminder(query)

    suspend fun addReminder(reminder: Reminder) = db.getReminderDao().addReminder(reminder)

    suspend fun updateReminder(reminder: Reminder) = db.getReminderDao().updateReminder(reminder)

    suspend fun deleteReminder(reminder: Reminder) = db.getReminderDao().deleteReminder(reminder)
}
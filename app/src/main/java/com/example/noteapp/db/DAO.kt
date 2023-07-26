package com.example.noteapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteapp.model.Note

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note : Note)


    @Update
    suspend fun updateNote(note : Note)

    @Query("select * from Note order by id desc")
    fun getAllNote():LiveData<List<Note>>
    @Query("select * from Note order by id desc")
    fun getAllNoteSync():List<Note>

    @Query("select * from Note where title like :query or content like :query or date like :query order by id desc ")
    fun searchNote(query:String ):LiveData<List<Note>>


    @Delete
    suspend fun deleteNote(note :Note)
}
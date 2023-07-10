package com.example.noteapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder

@Database(
    entities = [Note::class,Reminder::class],
    version = 2,
    exportSchema = false
)
abstract class NoteDB : RoomDatabase() {

    abstract fun getNoteDao():DAO

    abstract fun getReminderDao():ReminderDAO

    companion object{

        @Volatile
        private var instance: NoteDB? = null
        private val LOCK = Any()

        operator fun invoke(context:Context) = instance?: synchronized(LOCK){
            instance?: createDatabase(context).also {
                instance = it
            }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,NoteDB::class.java,"note_db"
        ).fallbackToDestructiveMigration().build()
    }
}
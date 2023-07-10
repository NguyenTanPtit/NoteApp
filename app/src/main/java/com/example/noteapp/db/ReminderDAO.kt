package com.example.noteapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteapp.model.Reminder

@Dao
interface ReminderDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(remind : Reminder)

    @Update
    suspend fun updateReminder(reminder : Reminder)

    @Query("select * from Reminder order by id desc")
    fun getAllReminder(): LiveData<List<Reminder>>


    @Query("select * from Reminder where title like :query or content like :query " +
            "or date like :query or time like :query order by id desc ")
    fun searchReminder(query:String ): LiveData<List<Reminder>>


    @Delete
    suspend fun deleteReminder(reminder : Reminder)
}
package com.example.noteapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    var Id:Int = 0,
    val title:String,
    val content :String,
    val date: String,
    val color: Int = -1,
    val time : String
    ): Serializable

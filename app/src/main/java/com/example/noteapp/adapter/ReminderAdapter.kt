package com.example.noteapp.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.model.Reminder

class ReminderAdapter: ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffUtilCallBackReminder()) {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}


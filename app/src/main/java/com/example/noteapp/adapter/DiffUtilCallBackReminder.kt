package com.example.noteapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.noteapp.model.Reminder

class DiffUtilCallBackReminder : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.Id == newItem.Id
    }
}
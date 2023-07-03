package com.example.noteapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.noteapp.model.Note

class DiffUtilCallback :DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.Id == newItem.Id
    }
}
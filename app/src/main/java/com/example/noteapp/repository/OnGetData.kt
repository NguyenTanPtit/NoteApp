package com.example.noteapp.repository

import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder

interface OnGetNoteData {
    fun onSuccessGetNote(res : MutableMap<Int, Note>)

}
interface OnGetDataReminder{
    fun onSuccessGetReminder(res : MutableMap<Int, Reminder>)
}
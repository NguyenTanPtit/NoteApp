package com.example.noteapp.viewModel

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.R
import com.example.noteapp.databinding.AlertDialogRequestPermissionBinding
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder
import com.example.noteapp.model.ReminderReceiver
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class NoteActivityViewModel(private val repo: NoteRepository) : ViewModel() {

    fun saveNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteNote(note)
    }

    fun searchNote(query: String): LiveData<List<Note>> {
        return repo.searchNote(query)
    }

    fun getAllNote(): LiveData<List<Note>> = repo.getNote()

    fun getAllRemind() : LiveData<List<Reminder>> = repo.getReminder()

    fun saveReminder(reminder: Reminder, context: Context, calendar: Calendar) =
        viewModelScope.launch {
            repo.addReminder(reminder)
            setNotifyReminder(reminder, context, calendar)
        }

    private fun setNotifyReminder(reminder: Reminder, context: Context?, calendar: Calendar) {

        askNotificationPermission(context)
        val i = Intent(context, ReminderReceiver::class.java)
        i.putExtra("title", reminder.title)
        i.putExtra("content", reminder.content)
        i.putExtra("time", reminder.time)
        i.putExtra("id", reminder.Id)

        val pendingIntent: PendingIntent = PendingIntent
            .getBroadcast(context, reminder.Id, i, PendingIntent.FLAG_IMMUTABLE)
        val alarm =
            context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun askNotificationPermission(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isPushNotificationGranted = context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            } == PackageManager.PERMISSION_GRANTED
            if (!isPushNotificationGranted) {
                displayUIRequestPermission(context)
            }
        }
    }

    private fun displayUIRequestPermission(context: Context?){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val inflater = LayoutInflater.from(context)
        val viewDialogBinding = AlertDialogRequestPermissionBinding.inflate(inflater)

        builder.setView(viewDialogBinding.root)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.bg_alert_dialog)

        viewDialogBinding.alertDialogOK.setOnClickListener {
            startActivity(context!!,
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", "com.example.noteapp", null)
                }, null
            )
            alertDialog.dismiss()
        }

        viewDialogBinding.alertDialogCancel.setOnClickListener {
            alertDialog.cancel()
        }
    }

    fun updateReminder(reminder: Reminder,context: Context,calendar: Calendar)=viewModelScope.launch{
        repo.updateReminder(reminder)
        setNotifyReminder(reminder,context,calendar)
    }
}
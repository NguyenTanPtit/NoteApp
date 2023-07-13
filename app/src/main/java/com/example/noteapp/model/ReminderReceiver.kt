package com.example.noteapp.model

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noteapp.R
import com.example.noteapp.activities.MainActivity

class ReminderReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val time = intent.getStringExtra("time")
        val notID = intent.getIntExtra("id",0)
        val activityIntent =Intent(context,MainActivity::class.java)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,
            0,activityIntent, PendingIntent.FLAG_IMMUTABLE)

        val chanelID = "chanel_id"
        val name : CharSequence = "Notification Reminder"
        val des = "description"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(chanelID,name, NotificationManager.IMPORTANCE_HIGH)
            channel.description = des
            val notificationManager: NotificationManager = context.getSystemService(
                NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        val notify : Notification = NotificationCompat.Builder(context,chanelID).setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentText("$content \n at $time")
            .setDeleteIntent(pendingIntent)
            .setGroup("Reminder").build()

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(notID,notify)
    }
}
package com.example.smartplanner.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val goal = intent.getStringExtra("goal") ?: "Задача"
        val id = intent.getIntExtra("id", 0)
        Log.d("ReminderReceiver", "Reminder received for id=$id, goal=$goal")

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(context, id, "Напоминание: $goal", "Пора выполнить задачу")
    }
}

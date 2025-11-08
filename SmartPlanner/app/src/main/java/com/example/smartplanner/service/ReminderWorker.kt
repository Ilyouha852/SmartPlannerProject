package com.example.smartplanner.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.work.ListenableWorker.Result

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_TASK_ID = "task_id"
        private const val TAG = "ReminderWorker"
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Напоминание"
        val body = inputData.getString(KEY_BODY) ?: "Пора выполнить задачу"
        val taskId = inputData.getInt(KEY_TASK_ID, 0)

        try {
            Log.d(TAG, "Showing notification for task=$taskId title=$title")
            NotificationHelper.createNotificationChannel(applicationContext)
            NotificationHelper.showNotification(applicationContext, taskId, title, body)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}

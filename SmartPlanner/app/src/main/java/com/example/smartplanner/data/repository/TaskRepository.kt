package com.example.smartplanner.data.repository

import android.content.Context
import com.example.smartplanner.data.local.TaskDao
import com.example.smartplanner.data.local.TaskEntity
import com.example.smartplanner.data.remote.ApiService
import com.example.smartplanner.data.dto.GenerateRequest
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.smartplanner.service.ReminderWorker
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepository(
    private val dao: TaskDao,
    private val api: ApiService,
    private val context: Context
) {
    fun getAllTasks() = dao.getAllTasks()

    suspend fun getTaskById(taskId: Int) = withContext(Dispatchers.IO) {
        dao.getTaskById(taskId)
    }

    // Inserts task and schedules reminder if reminderTime is set
    suspend fun addTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        val rowId = dao.insertTask(task)
        if (task.reminderTime != null) {
            val scheduled = task.copy(id = rowId.toInt())
            scheduleWorkForTask(scheduled)
        }
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        dao.updateTask(task)
        // Reschedule reminder if exists or cancel
        if (task.reminderTime != null) {
            scheduleWorkForTask(task)
        } else {
            cancelWorkForTask(task.id)
        }
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        dao.deleteTask(task)
        // Cancel any existing reminder
        cancelWorkForTask(task.id)
    }

    suspend fun generatePlan(request: GenerateRequest) = withContext(Dispatchers.IO) {
        api.generatePlan(request)
    }

    private fun scheduleWorkForTask(task: TaskEntity) {
        val name = "reminder_task_${task.id}"
        val now = System.currentTimeMillis()
        val delay = (task.reminderTime ?: now) - now
        val initialDelay = if (delay > 0) delay else 0L

        val input = workDataOf(
            ReminderWorker.KEY_TITLE to task.goal,
            ReminderWorker.KEY_BODY to ("Пора выполнить задачу"),
            ReminderWorker.KEY_TASK_ID to task.id
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            name,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelWorkForTask(taskId: Int) {
        val name = "reminder_task_${taskId}"
        WorkManager.getInstance(context).cancelUniqueWork(name)
    }
}
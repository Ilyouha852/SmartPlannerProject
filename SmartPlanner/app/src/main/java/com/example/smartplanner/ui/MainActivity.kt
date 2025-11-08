package com.example.smartplanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.smartplanner.data.local.TaskDatabase
import com.example.smartplanner.data.remote.RetrofitClient
import com.example.smartplanner.data.repository.TaskRepository
import com.example.smartplanner.ui.screens.MainScreen
import com.example.smartplanner.ui.theme.SmartPlannerTheme
import com.example.smartplanner.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        val db = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_db"
        ).build()

    val repository = TaskRepository(db.taskDao(), RetrofitClient.apiService, applicationContext)
        val viewModel = TaskViewModel(repository)

        setContent {
            SmartPlannerTheme {
                MainScreen(viewModel)
            }
        }
    }
}
package com.example.smartplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import com.example.smartplanner.data.local.TaskEntity
import com.example.smartplanner.ui.components.EmptyTasksPlaceholder
import com.example.smartplanner.ui.components.EditTaskDialog
import com.example.smartplanner.ui.components.TaskCard
import com.example.smartplanner.viewmodel.TaskViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlanListScreen(viewModel: TaskViewModel) {
    var showNewDialog by remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (tasks.isEmpty()) {
            EmptyTasksPlaceholder()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id.toLong() }
                ) { task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        TaskCard(
                            task = task,
                            onEdit = { viewModel.selectTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showNewDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }

    // Edit dialog
    selectedTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { viewModel.selectTask(null) },
            onConfirm = { updatedGoal: String, reminderTime: Long? ->
                viewModel.updateTask(task.id, updatedGoal, reminderTime)
                viewModel.selectTask(null)
            }
        )
    }

    if (showNewDialog) {
        // Temporary TaskEntity used as template for dialog
        val temp = TaskEntity(goal = "", planJson = "", reminderTime = null)
        EditTaskDialog(
            task = temp,
            onDismiss = { showNewDialog = false },
            onConfirm = { newGoal: String, reminderTime: Long? ->
                viewModel.addTask(newGoal, "", reminderTime)
                showNewDialog = false
            }
        )
    }
}

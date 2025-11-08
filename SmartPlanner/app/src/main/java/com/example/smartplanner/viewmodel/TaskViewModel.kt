package com.example.smartplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartplanner.data.dto.DayPlan
import com.example.smartplanner.data.dto.GenerateRequest
import com.example.smartplanner.data.dto.GenerateResponse
import com.example.smartplanner.data.local.TaskEntity
import com.example.smartplanner.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _plan = MutableStateFlow<List<DayPlan>>(emptyList())
    val plan: StateFlow<List<DayPlan>> = _plan

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedTask = MutableStateFlow<TaskEntity?>(null)
    val selectedTask: StateFlow<TaskEntity?> = _selectedTask

    fun fetchPlan(goal: String, days: Int = 3) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.generatePlan(GenerateRequest(goal = goal, days = days))
                _plan.value = response.days
            } catch (e: Exception) {
                e.printStackTrace()
                _plan.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                repository.getAllTasks().collect { tasks ->
                    _tasks.value = tasks
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _tasks.value = emptyList()
            }
        }
    }

    fun updateTask(taskId: Int, updatedGoal: String, reminderTime: Long?) {
        viewModelScope.launch {
            try {
                val task = repository.getTaskById(taskId) ?: return@launch
                val updated = task.copy(goal = updatedGoal, reminderTime = reminderTime)
                repository.updateTask(updated)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTask(goal: String, planJson: String = "", reminderTime: Long? = null) {
        viewModelScope.launch {
            try {
                val task = TaskEntity(goal = goal, planJson = planJson, reminderTime = reminderTime)
                repository.addTask(task)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectTask(task: TaskEntity?) {
        _selectedTask.value = task
    }
}

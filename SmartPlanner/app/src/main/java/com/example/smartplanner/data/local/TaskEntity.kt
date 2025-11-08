package com.example.smartplanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goal: String,
    val planJson: String,
    // reminderTime in millis since epoch. null means no reminder set
    val reminderTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
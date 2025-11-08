package com.example.smartplanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenerateRequest(
    val goal: String,
    val days: Int = 3
)

@Serializable
data class DayPlan(
    val day: Int,
    val tasks: List<String>
)

@Serializable
data class GenerateResponse(
    val days: List<DayPlan>
)

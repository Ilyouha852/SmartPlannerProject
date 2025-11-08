package com.example.smartplanner.data.remote

import com.example.smartplanner.data.dto.GenerateRequest
import com.example.smartplanner.data.dto.GenerateResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("generate_plan")
    suspend fun generatePlan(@Body request: GenerateRequest): GenerateResponse
}
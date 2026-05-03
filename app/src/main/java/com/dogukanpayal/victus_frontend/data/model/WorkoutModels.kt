package com.dogukanpayal.victus_frontend.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exercise(
    val id: String,
    val name: String,
    @Json(name = "met_value") val metValue: Double,
    val type: String? = null
)

@JsonClass(generateAdapter = true)
data class WorkoutLogRequest(
    @Json(name = "exercise_id") val exerciseId: String,
    @Json(name = "duration_minutes") val durationMinutes: Int
)

@JsonClass(generateAdapter = true)
data class WorkoutLogResponse(
    val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "exercise_id") val exerciseId: String,
    @Json(name = "duration_mins") val durationMins: Int,
    @Json(name = "calories_burned") val caloriesBurned: Double,
    val date: String?
)

@JsonClass(generateAdapter = true)
data class WorkoutPreset(
    val id: String,
    val name: String,
    val exercises: List<WorkoutPresetExercise>
)

@JsonClass(generateAdapter = true)
data class WorkoutPresetExercise(
    val id: String,
    val exerciseId: String,
    val exerciseName: String,
    val isDurationBased: Boolean,
    val durationMinutes: Int?,
    val sets: Int?,
    val reps: Int?
)

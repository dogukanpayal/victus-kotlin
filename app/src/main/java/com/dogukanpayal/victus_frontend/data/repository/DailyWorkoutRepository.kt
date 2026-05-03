package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyWorkoutRepository(context: Context) {
    private val prefs = context.getSharedPreferences("daily_workout_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACTIVE_PRESET_IDS = "active_preset_ids"
        private const val KEY_COMPLETED_EXERCISES = "completed_exercises"
        private const val KEY_COMPLETED_LOGS = "completed_logs"
        private const val KEY_LAST_DATE = "last_date"
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun checkAndResetIfNewDay() {
        val lastDate = prefs.getString(KEY_LAST_DATE, "")
        val today = getCurrentDateString()
        if (lastDate != today) {
            // It's a new day, clear progress
            prefs.edit()
                .remove(KEY_ACTIVE_PRESET_IDS)
                .remove(KEY_COMPLETED_EXERCISES)
                .remove(KEY_COMPLETED_LOGS)
                .putString(KEY_LAST_DATE, today)
                .apply()
        }
    }

    fun getActivePresetIds(): List<String> {
        checkAndResetIfNewDay()
        return try {
            val str = prefs.getString(KEY_ACTIVE_PRESET_IDS, "") ?: ""
            if (str.isEmpty()) emptyList() else str.split(",")
        } catch (e: Exception) {
            // Eski veri tipi (Set<String>) kalmış olabilir, temizle veya dönüştür
            val oldSet = try { prefs.getStringSet(KEY_ACTIVE_PRESET_IDS, emptySet()) } catch (e: Exception) { emptySet<String>() }
            val newList = oldSet?.toList() ?: emptyList()
            prefs.edit().putString(KEY_ACTIVE_PRESET_IDS, newList.joinToString(",")).apply()
            newList
        }
    }

    fun addActivePresetId(presetId: String) {
        checkAndResetIfNewDay()
        val currentList = getActivePresetIds().toMutableList()
        currentList.add(presetId)
        prefs.edit().putString(KEY_ACTIVE_PRESET_IDS, currentList.joinToString(",")).apply()
    }

    fun removeActivePresetId(presetId: String) {
        checkAndResetIfNewDay()
        val currentList = getActivePresetIds().toMutableList()
        currentList.remove(presetId) // Removes only the first occurrence
        prefs.edit().putString(KEY_ACTIVE_PRESET_IDS, currentList.joinToString(",")).apply()
    }

    fun getCompletedExerciseIds(): Set<String> {
        checkAndResetIfNewDay()
        return prefs.getStringSet(KEY_COMPLETED_EXERCISES, emptySet()) ?: emptySet()
    }

    fun getCompletedLogs(): Map<String, String> {
        checkAndResetIfNewDay()
        val set = prefs.getStringSet(KEY_COMPLETED_LOGS, emptySet()) ?: emptySet()
        return set.associate { 
            val parts = it.split(":")
            if (parts.size >= 2) parts[0] to parts[1] else it to ""
        }
    }

    fun addCompletedExercise(presetExerciseId: String, logId: String) {
        checkAndResetIfNewDay()
        val currentExercises = getCompletedExerciseIds().toMutableSet()
        currentExercises.add(presetExerciseId)
        
        val currentLogs = (prefs.getStringSet(KEY_COMPLETED_LOGS, emptySet()) ?: emptySet()).toMutableSet()
        // Remove old entry if exists for this exercise
        currentLogs.removeAll { it.startsWith("$presetExerciseId:") }
        currentLogs.add("$presetExerciseId:$logId")
        
        prefs.edit()
            .putStringSet(KEY_COMPLETED_EXERCISES, currentExercises)
            .putStringSet(KEY_COMPLETED_LOGS, currentLogs)
            .commit()
    }

    fun removeCompletedExercise(presetExerciseId: String) {
        checkAndResetIfNewDay()
        val currentExercises = getCompletedExerciseIds().toMutableSet()
        currentExercises.remove(presetExerciseId)
        
        val currentLogs = (prefs.getStringSet(KEY_COMPLETED_LOGS, emptySet()) ?: emptySet()).toMutableSet()
        currentLogs.removeAll { it.startsWith("$presetExerciseId:") }
        
        prefs.edit()
            .putStringSet(KEY_COMPLETED_EXERCISES, currentExercises)
            .putStringSet(KEY_COMPLETED_LOGS, currentLogs)
            .commit()
    }

    fun clearCompletedExercises() {
        checkAndResetIfNewDay()
        prefs.edit()
            .remove(KEY_COMPLETED_EXERCISES)
            .remove(KEY_COMPLETED_LOGS)
            .commit()
    }
}

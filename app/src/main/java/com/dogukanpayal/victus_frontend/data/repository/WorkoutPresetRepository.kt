package com.dogukanpayal.victus_frontend.data.repository

import android.content.Context
import com.dogukanpayal.victus_frontend.data.model.WorkoutPreset
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WorkoutPresetRepository(context: Context) {
    private val prefs = context.getSharedPreferences("workout_presets", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, WorkoutPreset::class.java)
    private val adapter = moshi.adapter<List<WorkoutPreset>>(type)

    fun getPresets(): List<WorkoutPreset> {
        val json = prefs.getString("presets", null) ?: return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun savePreset(preset: WorkoutPreset) {
        val currentPresets = getPresets().toMutableList()
        // If preset with same ID exists, update it, else add it
        val index = currentPresets.indexOfFirst { it.id == preset.id }
        if (index != -1) {
            currentPresets[index] = preset
        } else {
            currentPresets.add(preset)
        }
        val json = adapter.toJson(currentPresets)
        prefs.edit().putString("presets", json).apply()
    }

    fun deletePreset(presetId: String) {
        val currentPresets = getPresets().toMutableList()
        currentPresets.removeAll { it.id == presetId }
        val json = adapter.toJson(currentPresets)
        prefs.edit().putString("presets", json).apply()
    }
}

package com.dogukanpayal.victus_frontend.data.model

enum class UserRole {
    PATIENT,
    DIETITIAN;

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.lowercase()) {
                "dietitian" -> DIETITIAN
                else -> PATIENT
            }
        }
    }
}

package com.dogukanpayal.victus_frontend.data.repository

import android.util.Log
import com.dogukanpayal.victus_frontend.data.model.AuthSession
import com.dogukanpayal.victus_frontend.data.model.LoginRequest
import com.dogukanpayal.victus_frontend.data.model.RegisterRequest
import com.dogukanpayal.victus_frontend.data.remote.RetrofitClient

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthSession>
    suspend fun register(email: String, password: String, fullName: String): Result<AuthSession>
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl : AuthRepository {
    private val TAG = "VictusAuth"

    // Mock Supabase client - In production, this would be properly initialized
    private val supabaseUrl = "https://your-project.supabase.co"
    private val supabaseKey = "your-anon-key"

    override suspend fun login(request: LoginRequest): Result<AuthSession> {
        Log.d(TAG, "Login attempt for: ${request.email}")
        return try {
            val response = RetrofitClient.apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val profile = loginResponse.user
                Log.d(TAG, "Login API Success: ${profile.email}, ID: ${profile.id}")
                
                val session = AuthSession(
                    accessToken = loginResponse.accessToken,
                    userId = profile.id,
                    email = profile.email
                )
                Result.success(session)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e(TAG, "Login API Error: ${response.code()} - $errorMsg")
                Result.failure(Exception("Login failed: E-posta veya şifre hatalı"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login API Exception: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, fullName: String): Result<AuthSession> {
        Log.d(TAG, "Registering user via API: $fullName ($email)")
        return try {
            val request = RegisterRequest(fullName = fullName, email = email, password = password)
            val response = RetrofitClient.apiService.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val registerResponse = response.body()!!
                val profile = registerResponse.user
                Log.d(TAG, "Registration API Success: ${profile.email}, ID: ${profile.id}")
                
                val session = AuthSession(
                    accessToken = registerResponse.accessToken,
                    userId = profile.id,
                    email = profile.email
                )
                Result.success(session)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e(TAG, "Registration API Error: ${response.code()} - $errorMsg")
                Result.failure(Exception("Registration failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration API Exception: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // Supabase logout
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

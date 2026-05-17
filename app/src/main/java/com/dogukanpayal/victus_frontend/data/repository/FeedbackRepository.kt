package com.dogukanpayal.victus_frontend.data.repository

import com.dogukanpayal.victus_frontend.data.model.FeedbackMessage
import com.dogukanpayal.victus_frontend.data.model.SendFeedbackRequest
import com.dogukanpayal.victus_frontend.data.remote.VictusApiService

interface FeedbackRepository {
    suspend fun getFeedbacks(token: String): Result<List<FeedbackMessage>>
    suspend fun markAsRead(token: String, id: String): Result<Unit>
    suspend fun sendFeedback(token: String, request: SendFeedbackRequest): Result<FeedbackMessage>
}

class FeedbackRepositoryImpl(private val apiService: VictusApiService) : FeedbackRepository {
    override suspend fun getFeedbacks(token: String): Result<List<FeedbackMessage>> {
        return try {
            val response = apiService.getFeedbacks("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch feedbacks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(token: String, id: String): Result<Unit> {
        return try {
            val response = apiService.markFeedbackAsRead("Bearer $token", id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendFeedback(token: String, request: SendFeedbackRequest): Result<FeedbackMessage> {
        return try {
            val response = apiService.sendFeedback("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to send feedback"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

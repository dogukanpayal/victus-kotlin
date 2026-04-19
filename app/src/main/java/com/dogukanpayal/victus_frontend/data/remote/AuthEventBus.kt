package com.dogukanpayal.victus_frontend.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Simple event bus to handle global authentication events like 401 Unauthorized
 */
object AuthEventBus {
    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents = _authEvents.asSharedFlow()

    suspend fun emit(event: AuthEvent) {
        _authEvents.emit(event)
    }
}

sealed class AuthEvent {
    object Unauthorized : AuthEvent()
}

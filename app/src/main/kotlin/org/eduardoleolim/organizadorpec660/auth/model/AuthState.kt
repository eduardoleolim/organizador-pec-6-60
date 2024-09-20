package org.eduardoleolim.organizadorpec660.auth.model

import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse

sealed class AuthState {
    data object Idle : AuthState()
    data object InProgress : AuthState()
    data class Success(val user: AuthUserResponse) : AuthState()
    data class Error(val error: Throwable) : AuthState()
}

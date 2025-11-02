package com.dolphin.expense.ui.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolphin.expense.auth.AuthManager
import com.dolphin.expense.auth.AuthResult
import com.dolphin.expense.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    val authState: StateFlow<AuthState> = authManager.authState

    fun isUserAuthenticated(): Boolean {
        return authManager.isUserAuthenticated()
    }

    suspend fun createSignInIntentSender(): IntentSender? {
        return authManager.createSignInIntentSender()
    }

    suspend fun signInWithIntent(intent: Intent): AuthResult {
        return authManager.signInWithIntent(intent)
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
        }
    }
}

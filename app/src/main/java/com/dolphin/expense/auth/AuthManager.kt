package com.dolphin.expense.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // Check if user is already signed in
        _currentUser.value = auth.currentUser
        _authState.value = if (auth.currentUser != null) {
            AuthState.Authenticated(auth.currentUser!!)
        } else {
            AuthState.Unauthenticated
        }
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    suspend fun createSignInIntentSender(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.Error(e.message ?: "Failed to create sign-in intent")
            null
        }
    }

    suspend fun signInWithIntent(intent: Intent): AuthResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken

            if (googleIdToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user

                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    AuthResult.Success(user)
                } else {
                    _authState.value = AuthState.Error("Sign in failed: No user")
                    AuthResult.Error("Sign in failed: No user")
                }
            } else {
                _authState.value = AuthState.Error("Sign in failed: No ID token")
                AuthResult.Error("Sign in failed: No ID token")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getWebClientId())
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun getWebClientId(): String {
        // This will read from google-services.json
        val resources = context.resources
        val resourceId = resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        return resources.getString(resourceId)
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

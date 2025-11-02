package com.dolphin.expense.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Helper class to manage FCM tokens
 */
object FCMTokenManager {
    private const val TAG = "FCMTokenManager"
    private const val PREFS_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    /**
     * Get the current FCM token
     * This will request a new token from Firebase if one doesn't exist
     */
    suspend fun getToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token retrieved: $token")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }

    /**
     * Get the saved FCM token from SharedPreferences
     */
    fun getSavedToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    /**
     * Save FCM token to SharedPreferences
     */
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "Token saved to SharedPreferences: $token")
    }

    /**
     * Delete the FCM token
     * This will invalidate the current token on the server
     */
    suspend fun deleteToken() {
        try {
            FirebaseMessaging.getInstance().deleteToken().await()
            Log.d(TAG, "FCM Token deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete FCM token", e)
        }
    }

    /**
     * Subscribe to a topic
     * Useful for sending notifications to groups of users
     */
    suspend fun subscribeToTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            Log.d(TAG, "Subscribed to topic: $topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topic: $topic", e)
        }
    }

    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            Log.d(TAG, "Unsubscribed from topic: $topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
        }
    }
}

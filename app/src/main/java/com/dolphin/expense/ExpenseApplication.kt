package com.dolphin.expense

import android.app.Application
import android.util.Log
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseApplication : Application() {

    companion object {
        private const val TAG = "ExpenseApplication"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase In-App Messaging
        initializeInAppMessaging()
    }

    /**
     * Initialize Firebase In-App Messaging with custom listeners
     */
    private fun initializeInAppMessaging() {
        try {
            val firebaseInAppMessaging = FirebaseInAppMessaging.getInstance()

            // Enable automatic data collection
            firebaseInAppMessaging.setMessagesSuppressed(false)

            // Add click listener for In-App Messages
            firebaseInAppMessaging.addClickListener(object : FirebaseInAppMessagingClickListener {
                override fun messageClicked(inAppMessage: InAppMessage, action: Action) {
                    Log.d(TAG, "In-App Message clicked!")
                    Log.d(TAG, "Campaign ID: ${inAppMessage.campaignMetadata?.campaignId}")
                    Log.d(TAG, "Campaign Name: ${inAppMessage.campaignMetadata?.campaignName}")
                    Log.d(TAG, "Action URL: ${action.actionUrl}")

                    // Handle different actions based on the action URL
                    action.actionUrl?.let { url ->
                        handleInAppMessageAction(url)
                    }
                }
            })

            Log.d(TAG, "Firebase In-App Messaging initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize In-App Messaging", e)
        }
    }

    /**
     * Handle custom actions from In-App Messages
     */
    private fun handleInAppMessageAction(actionUrl: String) {
        Log.d(TAG, "Handling action URL: $actionUrl")

        // You can handle custom deep links here
        // For example:
        // - expense://add - Navigate to add expense screen
        // - expense://budget - Navigate to budget screen
        // - expense://settings - Navigate to settings screen

        when {
            actionUrl.startsWith("expense://") -> {
                val screen = actionUrl.removePrefix("expense://")
                Log.d(TAG, "Navigate to screen: $screen")
                // TODO: Implement navigation handling
            }
            actionUrl.startsWith("http://") || actionUrl.startsWith("https://") -> {
                Log.d(TAG, "Open web URL: $actionUrl")
                // TODO: Open URL in browser
            }
            else -> {
                Log.d(TAG, "Unknown action: $actionUrl")
            }
        }
    }
}
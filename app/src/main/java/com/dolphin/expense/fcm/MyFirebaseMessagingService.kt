package com.dolphin.expense.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dolphin.expense.MainActivity
import com.dolphin.expense.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service
 * Handles incoming push notifications and FCM token updates
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "expense_tracker_notifications"
        private const val CHANNEL_NAME = "Expense Tracker Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for expense reminders and updates"
    }

    /**
     * Called when a new FCM token is generated
     * This happens on app install, token refresh, or when the user clears app data
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")

        // TODO: Send token to your server if you have a backend
        // You can save it to SharedPreferences for later use
        saveTokenToPreferences(token)
    }

    /**
     * Called when a message is received from Firebase
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Check if message contains a notification payload
        message.notification?.let { notification ->
            Log.d(TAG, "Notification Title: ${notification.title}")
            Log.d(TAG, "Notification Body: ${notification.body}")

            showNotification(
                title = notification.title ?: "Expense Tracker",
                body = notification.body ?: "",
                data = message.data
            )
        }

        // Check if message contains a data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")

            // Handle data payload
            handleDataPayload(message.data)
        }
    }

    /**
     * Handle data payload from FCM
     */
    private fun handleDataPayload(data: Map<String, String>) {
        // Extract custom data
        val title = data["title"] ?: "Expense Tracker"
        val body = data["body"] ?: "You have a new notification"
        val type = data["type"] // e.g., "reminder", "budget_alert", etc.

        Log.d(TAG, "Data - Title: $title, Body: $body, Type: $type")

        // Show notification with custom data
        showNotification(title, body, data)
    }

    /**
     * Display a notification to the user
     */
    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        createNotificationChannel(notificationManager)

        // Create intent to open the app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add any extra data from the notification
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // You can create a custom notification icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        // Show the notification
        val notificationId = System.currentTimeMillis().toInt() // Unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())

        Log.d(TAG, "Notification displayed with ID: $notificationId")
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    /**
     * Save FCM token to SharedPreferences
     */
    private fun saveTokenToPreferences(token: String) {
        val sharedPrefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "Token saved to SharedPreferences")
    }
}

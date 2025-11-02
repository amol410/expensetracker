package com.dolphin.expense.inappmessaging

import android.util.Log
import com.google.firebase.inappmessaging.FirebaseInAppMessaging

/**
 * Manager class for Firebase In-App Messaging
 * Provides helper methods to control and trigger in-app messages
 */
object InAppMessagingManager {

    private const val TAG = "InAppMessagingManager"

    /**
     * Enable In-App Messaging
     * Messages will be displayed according to campaign settings
     */
    fun enableMessages() {
        try {
            FirebaseInAppMessaging.getInstance().setMessagesSuppressed(false)
            Log.d(TAG, "In-App Messaging enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable In-App Messaging", e)
        }
    }

    /**
     * Disable In-App Messaging
     * No messages will be displayed until enabled again
     */
    fun disableMessages() {
        try {
            FirebaseInAppMessaging.getInstance().setMessagesSuppressed(true)
            Log.d(TAG, "In-App Messaging disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disable In-App Messaging", e)
        }
    }

    /**
     * Enable automatic data collection for In-App Messaging
     */
    fun enableAutomaticDataCollection() {
        try {
            FirebaseInAppMessaging.getInstance().isAutomaticDataCollectionEnabled = true
            Log.d(TAG, "Automatic data collection enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable automatic data collection", e)
        }
    }

    /**
     * Disable automatic data collection for In-App Messaging
     */
    fun disableAutomaticDataCollection() {
        try {
            FirebaseInAppMessaging.getInstance().isAutomaticDataCollectionEnabled = false
            Log.d(TAG, "Automatic data collection disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disable automatic data collection", e)
        }
    }

    /**
     * Trigger a programmatic event
     * This can be used to trigger In-App Messages based on custom events
     *
     * @param eventName The name of the event to trigger
     *
     * Example events you can define in Firebase Console:
     * - "expense_added" - When user adds an expense
     * - "budget_exceeded" - When user exceeds their budget
     * - "weekly_summary" - When viewing weekly summary
     * - "settings_opened" - When settings screen is opened
     */
    fun triggerEvent(eventName: String) {
        try {
            FirebaseInAppMessaging.getInstance().triggerEvent(eventName)
            Log.d(TAG, "Triggered In-App Messaging event: $eventName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to trigger event: $eventName", e)
        }
    }

    /**
     * Common event triggers for the Expense Tracker app
     */
    object Events {
        const val EXPENSE_ADDED = "expense_added"
        const val BUDGET_SET = "budget_set"
        const val BUDGET_EXCEEDED = "budget_exceeded"
        const val BUDGET_WARNING = "budget_warning_75_percent"
        const val WEEKLY_SUMMARY_VIEWED = "weekly_summary_viewed"
        const val MONTHLY_SUMMARY_VIEWED = "monthly_summary_viewed"
        const val SETTINGS_OPENED = "settings_opened"
        const val STATISTICS_VIEWED = "statistics_viewed"
        const val FIRST_EXPENSE = "first_expense_added"
        const val EXPENSE_DELETED = "expense_deleted"
        const val CURRENCY_CHANGED = "currency_changed"
    }
}

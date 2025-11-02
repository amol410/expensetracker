package com.dolphin.expense

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.dolphin.expense.fcm.FCMTokenManager
import com.dolphin.expense.navigation.AppNavigation
import com.dolphin.expense.ui.theme.ExpenseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // Register for notification permission result (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            getFCMToken()
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission and get FCM token
        requestNotificationPermissionAndGetToken()

        setContent {
            ExpenseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * Request notification permission for Android 13+ and get FCM token
     */
    private fun requestNotificationPermissionAndGetToken() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    Log.d(TAG, "Notification permission already granted")
                    getFCMToken()
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For Android 12 and below, notification permission is granted by default
            getFCMToken()
        }
    }

    /**
     * Get FCM token and log it
     */
    private fun getFCMToken() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = FCMTokenManager.getToken()
            if (token != null) {
                Log.d(TAG, "=================================")
                Log.d(TAG, "FCM Token: $token")
                Log.d(TAG, "=================================")

                // Save token to preferences
                FCMTokenManager.saveToken(this@MainActivity, token)

                // Optional: Subscribe to a default topic for all users
                // FCMTokenManager.subscribeToTopic("all_users")
            } else {
                Log.e(TAG, "Failed to retrieve FCM token")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    ExpenseTheme {
        AppNavigation()
    }
}
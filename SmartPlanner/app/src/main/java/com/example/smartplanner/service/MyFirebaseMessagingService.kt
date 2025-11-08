package com.example.smartplanner.service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        const val PREFS = "fcm_prefs"
        const val TOKEN_KEY = "fcm_token"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(TOKEN_KEY, token).apply()
        // TODO: send token to your server to enable push notifications targeting this device
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received: data=${remoteMessage.data}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Напоминание"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Пора выполнить задачу"

        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.showNotification(this, 1001, title, body)
    }
}

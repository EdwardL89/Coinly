package com.eightnineapps.coinly.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.views.activities.startup.SplashScreenActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val authHelper = AuthHelper()

    /**
     * Creates and sends an Android notification to the local device only if the intended receiver
     * of the FCM is currently logged in
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (authHelper.getAuthUser() != null && authHelper.getAuthUserEmail() == remoteMessage.data["receiver"]) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(0, constructAndroidNotification(remoteMessage).build())
        }
    }

    /**
     * Replaces the current user's token if it's compromised (security)
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (CurrentUser.getEmail() != null) Firestore.update(CurrentUser.getEmail()!!, "token", token)
    }

    /**
     * Constructs an Android notification and returns the builder
     */
    private fun constructAndroidNotification(remoteMessage: RemoteMessage): NotificationCompat.Builder {
        val notificationIntent = Intent(this, SplashScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT )
        return NotificationCompat.Builder(this, "123")
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.coinly_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }
}

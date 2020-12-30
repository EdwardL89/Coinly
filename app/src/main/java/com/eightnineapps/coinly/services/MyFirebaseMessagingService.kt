package com.eightnineapps.coinly.services

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("INFO12", "From: " + remoteMessage.from);


        Log.d("INFO12", "Message data payload: " + remoteMessage.data);


        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d("INFO12", "Message Notification Body: " + remoteMessage.notification!!.body);
        }

    }
}
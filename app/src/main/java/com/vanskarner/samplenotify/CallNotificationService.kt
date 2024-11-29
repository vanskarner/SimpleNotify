package com.vanskarner.samplenotify

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.vanskarner.simplenotify.SimpleNotify

class CallNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread.sleep(5000)
        val notificationPair = SimpleNotify.with(this)
            .asCall {
                type = "incoming"
//                type = "screening"
//                type = "ongoing"
//                caller = Person.Builder().setName("Juan").build()
//                answer = null
//                declineOrHangup = null
            }
            .generateNotificationPair()
        startForeground(notificationPair.first, notificationPair.second.build())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getSimplePendingIntent(clazz: Class<out Activity>): PendingIntent {
        val intent = Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

}
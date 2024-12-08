package com.vanskarner.samplenotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vanskarner.simplenotify.SimpleNotify

class NotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notification_id", -1) ?: return
        SimpleNotify.cancel(context, notificationId)
    }

}
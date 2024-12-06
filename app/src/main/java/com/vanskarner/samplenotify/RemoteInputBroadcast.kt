package com.vanskarner.samplenotify

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.RemoteInput
import com.vanskarner.samplenotify.styles.basic.BASIC_TYPE
import com.vanskarner.samplenotify.styles.duomessaging.DUO_MESSAGING_TYPE
import com.vanskarner.simplenotify.SimpleNotify

class RemoteInputBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val msg = getMessageText(intent)
        val notificationId = intent.getIntExtra(BaseActivity.INTENT_EXTRA_NOTIFY_ID, -1)
        val activityType = intent.getStringExtra(BaseActivity.INTENT_EXTRA_ACTIVITY)
        when (activityType) {
            BASIC_TYPE -> {
                SimpleNotify
                    .with(context)
                    .asBasic {
                        id = notificationId
                        title = "Message sent"
                        text = "Your message has been sent: $msg"
                        timeoutAfter = 1000L
                    }
                    .show()
            }

            DUO_MESSAGING_TYPE -> {
                val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.cancelAll()
            }

            else -> {
                val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.cancelAll()
            }
        }
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence(BaseActivity.REMOTE_INPUT_KEY)
    }

}
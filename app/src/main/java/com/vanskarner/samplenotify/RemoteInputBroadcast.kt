package com.vanskarner.samplenotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput

class RemoteInputBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val msg = getMessageText(intent)
        val notificationId = intent.getIntExtra(BaseActivity.INTENT_EXTRA_NOTIFY_ID, -1)
        SimpleNotify
            .with(context)
            .asBasic {
                id = notificationId
                title = "Message sent"
                text = "Your message has been sent: $msg"
            }
//            .extras {
//                timeoutAfter = 2500
//            }
            .show()
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence(BaseActivity.REMOTE_INPUT_KEY)
    }

}
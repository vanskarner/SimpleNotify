package com.vanskarner.samplenotify

import android.content.Context
import com.vanskarner.samplenotify.internal.NotifyChannel

class SimpleNotify {

    companion object {

        private val notifyChannel = NotifyChannel

        fun with(context: Context): NotifyConfig {
            return NotifyConfig(context)
        }

        fun cancel(context: Context, channelId: Int) {
            notifyChannel.cancelNotification(context, channelId)
        }

    }

}
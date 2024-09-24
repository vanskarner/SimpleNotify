package com.vanskarner.samplenotify

import android.content.Context
import com.vanskarner.samplenotify.internal.NotifyChannel

class SimpleNotify {

    companion object {

        fun with(context: Context): NotifyConfig {
            return NotifyConfig(context)
        }

        fun cancel(context: Context, channelId: Int) {
            NotifyChannel(context).cancelNotification(channelId)
        }

    }

}
package com.vanskarner.samplenotify

import android.content.Context

class SimpleNotify {
    companion object {
        fun with(context: Context): NotificationConfig {
            return NotificationConfig(context)
        }
    }
}
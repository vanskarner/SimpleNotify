package com.vanskarner.samplenotify

import android.content.Context

class SimpleNotify {
    companion object {
        fun with(context: Context): NotifyConfig {
            return NotifyConfig(context)
        }
    }
}
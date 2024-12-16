package com.vanskarner.simplenotify

import androidx.core.app.NotificationCompat

interface Notify {
    fun show(): Pair<Int, Int>
    fun generateBuilder(): NotificationCompat.Builder?
}
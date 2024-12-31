package com.vanskarner.simplenotify.internal

import androidx.core.app.NotificationCompat

internal interface Notify {
    fun show(): Pair<Int, Int>
    fun generateBuilder(): NotificationCompat.Builder?
}
package com.vanskarner.simplenotify.types

import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.Notify
import com.vanskarner.simplenotify.internal.INVALID_NOTIFICATION_ID

class InvalidNotify : Notify {

    override fun show(): Pair<Int, Int> = Pair(INVALID_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)

    override fun generateBuilder(): NotificationCompat.Builder? = null

}
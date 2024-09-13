package com.vanskarner.samplenotify

import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.R

data class BasicData(
    var smallIcon: Int = R.drawable.baseline_notifications_24,
    var title: String? = null,
    var text: String? = null,
    var importance: Int = NotificationCompat.PRIORITY_DEFAULT,
    var click: PendingIntent? = null
)

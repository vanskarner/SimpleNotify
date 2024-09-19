package com.vanskarner.samplenotify

import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.R

sealed class Data {
    var id: Int? = null
    var smallIcon: Int = R.drawable.baseline_notifications_24
    var title: String? = null
    var largeIcon: Bitmap? = null
    var importance: Int = NotificationCompat.PRIORITY_DEFAULT
    var pending: PendingIntent? = null
    var autoCancel: Boolean = true
    var sound: Uri? = null

    data class BasicData(
        var text: String? = null
    ) : Data()

    data class BigTextData(
        var bigText: String? = null,
        var collapsedText: String? = null,
        var summaryText: String? = null,
    ) : Data()
}

data class ActionData(
    var icon: Int = R.drawable.baseline_notifications_24,
    var name: String? = null,
    var pending: PendingIntent? = null
)

data class ChannelData(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
)

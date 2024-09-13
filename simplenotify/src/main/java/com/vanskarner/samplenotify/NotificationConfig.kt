package com.vanskarner.samplenotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationConfig(internal val context: Context) {
    internal var basicData = BasicData()
    internal var channelId = "default_channel"
    private lateinit var notifyBuilder: NotificationCompat.Builder

    fun asBasic(content: BasicData.() -> Unit): NotificationConfig {
        basicData = BasicData().apply(content)
        return this
    }

    fun show() {
        notifyBuilder = buildSimpleNotify(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(channelId)

        val notificationId = 12
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notificationId, notifyBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String = "default_chanel") {
        val name = getText(R.string.chanel_name)
        val descriptionText = getText(R.string.chanel_text)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
            .apply { description = descriptionText }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getText(resId: Int) = context.getString(resId)

}
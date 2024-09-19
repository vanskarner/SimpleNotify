package com.vanskarner.samplenotify.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.samplenotify.Data
import com.vanskarner.simplenotify.R
import kotlin.random.Random

internal abstract class Notify<T : Data>(val notifyData: NotifyData<T>) {
    companion object {
        private const val DEFAULT_CHANNEL_ID = "defaultId"
        private const val MAXIMUM_ACTIONS = 3
    }
    private val builder = NotificationCompat.Builder(notifyData.context, DEFAULT_CHANNEL_ID)

    fun show() {
        applyData(builder)
        applyPending()
        applyActions()
        applyChannel()
        with(NotificationManagerCompat.from(notifyData.context)) {
            if (ActivityCompat.checkSelfPermission(
                    notifyData.context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notifyData.data.id?: Random.nextInt(), builder.build())
        }
    }

    abstract fun applyData(builder: NotificationCompat.Builder)

    private fun applyPending() {
        builder.setContentIntent(notifyData.data.pending)
    }

    private fun applyActions() =
        notifyData.actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { action -> builder.addAction(action.icon, action.name, action.pending) }

    private fun applyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createDefaultChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDefaultChannel() {
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            getString(R.string.chanel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = getString(R.string.chanel_text) }
        val notificationManager = getNotificationManager()
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotificationManager() =
        notifyData.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private fun getString(stringId: Int) = notifyData.context.getString(stringId)

}
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

internal abstract class NotifyBase<T : Data>(val payLoad: PayLoadData<T>) {
    companion object {
        private const val DEFAULT_CHANNEL_ID = "defaultId"
        private const val MAXIMUM_ACTIONS = 3
    }

    protected val builder = NotificationCompat.Builder(payLoad.context, DEFAULT_CHANNEL_ID)

    abstract fun applyData()

    fun show() {
        applyData()
        applyPending()
        applyChannel()
        applyActions()
        with(NotificationManagerCompat.from(payLoad.context)) {
            if (ActivityCompat.checkSelfPermission(
                    payLoad.context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(12, builder.build())
        }
    }

    private fun applyPending() {
        builder.setContentIntent(payLoad.pending)
    }

    private fun applyActions() =
        payLoad.actions
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
        payLoad.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private fun getString(stringId: Int) = payLoad.context.getString(stringId)

}
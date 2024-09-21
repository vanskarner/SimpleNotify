package com.vanskarner.samplenotify.internal

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vanskarner.samplenotify.Data
import kotlin.random.Random

internal abstract class Notify<T : Data>(val notifyData: NotifyData<T>) {
    companion object {
        const val MAXIMUM_ACTIONS = 3
    }

    fun show() {
        val notifyBuilder = NotifyChannel(notifyData.context).applyChannel(notifyData.channelId)
        applyData(notifyBuilder)
        applyActions(notifyBuilder)
        with(NotificationManagerCompat.from(notifyData.context)) {
            if (ActivityCompat.checkSelfPermission(
                    notifyData.context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notifyData.data.id ?: Random.nextInt(), notifyBuilder.build())
        }
    }

    abstract fun applyData(builder: NotificationCompat.Builder)

    private fun applyActions(builder: NotificationCompat.Builder) =
        notifyData.actions
            .takeLast(MAXIMUM_ACTIONS)
            .filterNotNull()
            .forEach { action -> builder.addAction(action.icon, action.name, action.pending) }

    private fun getString(stringId: Int) = notifyData.context.getString(stringId)

}
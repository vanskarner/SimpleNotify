package com.vanskarner.simplenotify.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.Notify
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.StackableData

internal class BasicNotify(
    private val context: Context,
    private val data: Data.BasicData?,
    private val progressData: ProgressData?,
    private val channelId: String?,
    extra: ExtraData,
    stackableData: StackableData?,
    actions: Array<ActionData?>,
) : Notify, BaseNotify(context, progressData, extra, stackableData, actions) {

    override fun show(): Pair<Int, Int> {
        val requiredData = data ?: return invalidNotificationResult()
        return notify(requiredData)
    }

    override fun generateBuilder(): NotificationCompat.Builder? {
        val myData = data ?: return null
        return createNotification(myData, selectChannelId())
    }

    override fun applyData(builder: NotificationCompat.Builder) {
        val requiredData = data ?: return
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(requiredData.title)
            .setContentText(requiredData.text)
    }

    override fun selectChannelId(): String {
        return when {
            progressData == null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyDefaultChannel(context)

            progressData != null && notifyChannel.checkChannelNotExists(context, channelId) ->
                notifyChannel.applyProgressChannel(context)

            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

}
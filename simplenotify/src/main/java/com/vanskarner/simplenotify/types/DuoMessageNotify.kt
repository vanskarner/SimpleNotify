package com.vanskarner.simplenotify.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutManagerCompat
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.Notify
import com.vanskarner.simplenotify.NotifyMessaging

internal class DuoMessageNotify(private val context: Context, private val configData: ConfigData) :
    Notify, BaseNotify(
    context,
    configData.progressData,
    configData.extras,
    configData.stackableData,
    configData.channelId,
    configData.actions
) {
    private val data = configData.data as Data.DuoMessageData

    override fun show(): Pair<Int, Int> = notify(data)

    override fun generateBuilder(): NotificationCompat.Builder =
        createNotification(data, selectChannelId())

    override fun applyData(builder: NotificationCompat.Builder) {
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        val style = NotificationCompat.MessagingStyle(data.you)
            .setGroupConversation(false)
        data.messages.forEachIndexed { index, item ->
            val message = when (item) {
                is NotifyMessaging.ContactMsg -> {
                    Message(item.msg, item.timestamp, data.contact)
                }

                is NotifyMessaging.YourMsg -> {
                    val person: Person? = null
                    Message(item.msg, item.timestamp, person)
                }
            }
            item.mimeData?.let { pair -> message.setData(pair.first, pair.second) }
            if (data.useHistoricMessage) {
                if (index == data.messages.lastIndex) style.addMessage(message)
                else style.addHistoricMessage(message)
            } else {
                style.addMessage(message)
            }
        }
        data.bubble?.let { builder.setBubbleMetadata(it) }
        data.shortcut?.let { shortcut ->
            if (data.addShortcutIfNotExists) {
                val shortcutList = ShortcutManagerCompat.getDynamicShortcuts(context)
                val shortcutFound = shortcutList.find { it.id == shortcut.id }
                if (shortcutFound == null)
                    ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
            }
            builder.setShortcutInfo(shortcut)
        }
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(style)
            .addPerson(data.contact)
    }

    override fun enableProgress(): Boolean = false

    override fun selectChannelId(): String {
        return when {
            notifyChannel.checkChannelNotExists(context, configData.channelId) ->
                notifyChannel.applyMessagingChannel(context)

            else -> configData.channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

}
package com.vanskarner.samplenotify.bubbles

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.net.toUri
import com.vanskarner.samplenotify.BasicBubbleActivity
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.R

@RequiresApi(Build.VERSION_CODES.N_MR1)
class SampleBubbleNotificationView(private val context: Context) {
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val CHANNEL_NEW_BUBBLE = "new_bubble"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    init {
        setUpNotificationChannels()
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }

    fun showNotification(
        title: String,
        mySelf: Person,
        message: Message,
        notificationId: Int
    ) {
        val contentUri =
            "app://simplenotify.vanskarner.com/message/${message.text.toString()}".toUri()
        val bubbleIntent = PendingIntent.getActivity(
            context,
            REQUEST_BUBBLE,
            Intent(context, BasicBubbleActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUri),
            flagUpdateCurrent()
        )
        val bubbleData =
            NotificationCompat.BubbleMetadata.Builder(bubbleIntent, message.person!!.icon!!)
                .setDesiredHeight(500)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build()
        val intent = Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .setData(contentUri)
        val shortcut = ShortcutInfoCompat.Builder(context, notificationId.toString())
            .setLongLived(true)
            .setIntent(intent)
            .setShortLabel(message.person!!.name!!)
            .setIcon(message.person!!.icon)
            .setPerson(message.person!!)
            .build()
        val style = NotificationCompat.MessagingStyle(mySelf)
            .setConversationTitle(title)
            .addMessage(message.text, message.timestamp, message.person)
        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CONTENT,
            Intent(context, MainActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUri),
            flagUpdateCurrent(mutable = true)
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_BUBBLE)
            .setBubbleMetadata(bubbleData)
            .setStyle(style)
            .setShortcutId(shortcut.id)
            .addPerson(message.person)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(contentIntent)
        notificationManager.notify(notificationId, builder.build())
    }

    private fun flagUpdateCurrent(mutable: Boolean = true): Int {
        return when {
            mutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE

            mutable -> PendingIntent.FLAG_UPDATE_CURRENT

            else -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    private fun setUpNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel(CHANNEL_NEW_BUBBLE) == null
        ) {
            val notificationChannel = NotificationChannel(
                CHANNEL_NEW_BUBBLE,
                "My Canal predeterminado",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}

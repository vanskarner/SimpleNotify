package com.vanskarner.samplenotify.types.groupmessaging

import android.app.PendingIntent
import android.content.Intent
import android.widget.ArrayAdapter
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.net.toUri
import com.vanskarner.samplenotify.BaseActivity.Companion.REMOTE_INPUT_KEY
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.databinding.MainActivityBinding
import com.vanskarner.simplenotify.NotifyMessaging
import com.vanskarner.simplenotify.SimpleNotify

fun showGroupMessagingTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Group messaging" to ::basic,
        "Group messaging with images" to ::withImages,
        "Group messaging with bubbles" to ::withBubbles
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    val notifyId = 70
    SimpleNotify.with(activity)
        .asGroupMessaging {
            id = notifyId
            conversationTitle = "Titulo del Grupo"
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("dina1.jpg"))
                .build()
            messages = arrayListOf(
                NotifyMessaging.YourMsg(
                    "Do you like my rolex?",
                    System.currentTimeMillis() - (3 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "Mensaje de Max",
                    System.currentTimeMillis() - (2 * 60 * 1000),
                    Person.Builder().setName("Max").build()
                ),
                NotifyMessaging.ContactMsg(
                    "Algun Otro mensaje de Albert",
                    System.currentTimeMillis() - (1 * 60 * 1000),
                    Person.Builder().setName("Albert").build()
                )
            )
        }
        .addReplyAction {
            title = "Respond"
            replyPending = activity.pendingIntentToCloseNotification(notifyId)
            remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
        }
        .addAction {
            title = "Mute"
            pending = activity.pendingIntentToCloseNotification(notifyId)
        }
        .show()
}

private fun withImages(activity: MainActivity) {
    val notifyId = 71
    SimpleNotify.with(activity)
        .asGroupMessaging {
            id = notifyId
            conversationTitle = "Titulo del Grupo"
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("dina1.jpg"))
                .build()
            messages = arrayListOf(
                NotifyMessaging.YourMsg(
                    "Do you like my rolex?",
                    System.currentTimeMillis() - (3 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "Mensaje de Max",
                    System.currentTimeMillis() - (2 * 60 * 1000),
                    Person.Builder().setName("Max").build()
                ),
                NotifyMessaging.ContactMsg(
                    "Algun Otro mensaje de Albert",
                    System.currentTimeMillis() - (1 * 60 * 1000),
                    Person.Builder().setName("Albert").build()
                ).setData(
                    "image/jpeg",
                    "content://com.vanskarner.samplenotify/photo/rolex_dina.jpg".toUri()
                )
            )
        }
        .addReplyAction {
            title = "Respond"
            replyPending = activity.pendingIntentToCloseNotification(notifyId)
            remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
        }
        .addAction {
            title = "Mute"
            pending = activity.pendingIntentToCloseNotification(notifyId)
        }
        .show()
}

private fun withBubbles(activity: MainActivity) {
    val notifyId = 72
    SimpleNotify.with(activity)
        .asGroupMessaging {
            id = notifyId
            conversationTitle = "Titulo del Grupo"
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("sample_avatar.jpg"))
                .build()
            messages = GroupBubbleActivity.groupBubbleMsgSamples()
            val contentUri =
                "https://android.example.com/chat/yourGroupChatId".toUri()
            val bubbleIntent = PendingIntent.getActivity(
                activity,
                70,
                Intent(activity, GroupBubbleActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
                    .setData(contentUri),
                activity.flagUpdateCurrent()
            )
            val groupIcon = activity.iconFromAssets("dina1.jpg")
            bubble = NotificationCompat.BubbleMetadata.Builder(bubbleIntent, groupIcon)
                .setDesiredHeight(500)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build()
            shortcut = ShortcutInfoCompat.Builder(activity, "group_1")
                .setLocusId(LocusIdCompat("group_1"))
                .setLongLived(true)
                .setIntent(
                    Intent(activity, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUri)
                )
                .setShortLabel("Nombre del grupo")
                .setIcon(groupIcon)
                .build()
        }
        .addReplyAction {
            title = "Respond"
            replyPending = activity.pendingIntentToCloseNotification(notifyId)
            remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
        }
        .addAction {
            title = "Mute"
            pending = activity.pendingIntentToCloseNotification(notifyId)
        }
        .show()
}

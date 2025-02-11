package com.vanskarner.samplenotify.types.duomessaging

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

fun showDuoMessagingTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Duo Messaging" to ::basic,
        "Duo Messaging with images" to ::withImages,
        "Duo Messaging with bubbles" to ::withBubbles
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    val notifyId = 60
    SimpleNotify.with(activity)
        .asDuoMessaging {
            id = notifyId
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("sample_avatar.jpg"))
                .build()
            contact = Person.Builder()
                .setName("Dina")
                .setIcon(activity.iconFromAssets("dina1.jpg"))
                .build()
            messages = arrayListOf(
                NotifyMessaging.YourMsg(
                    " President, what do you think about the fact that most people call you 'Dina the Killer'?",
                    System.currentTimeMillis() - (3 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "I ask those who shout these words, who have killed our brothers in these violent demonstrations?",
                    System.currentTimeMillis() - (1 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "It was they themselves",
                    System.currentTimeMillis() - (1 * 60 * 1000)
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
    val notifyId = 61
    SimpleNotify.with(activity)
        .asDuoMessaging {
            id = notifyId
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("sample_avatar.jpg"))
                .build()
            contact = Person.Builder()
                .setName("Dina")
                .setIcon(activity.iconFromAssets("dina1.jpg"))
                .build()
            messages = arrayListOf(
                NotifyMessaging.YourMsg(
                    "President, the situation is tough, there's a lack of money, and insecurity is very high.",
                    System.currentTimeMillis() - (3 * 60 * 1000)
                ),
                NotifyMessaging.ContactMsg(
                    "What do you think of my Rolex, isn't it pretty?",
                    System.currentTimeMillis()
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
    val notifyId = 62
    SimpleNotify.with(activity)
        .asDuoMessaging {
            id = notifyId
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            you = Person.Builder()
                .setName("You")
                .setIcon(activity.iconFromAssets("sample_avatar.jpg"))
                .build()
            val contactIcon = activity.iconFromAssets("dina1.jpg")
            contact = Person.Builder()
                .setName("Dina Balearte")
                .setIcon(contactIcon)
                .build()
            messages = DuoBubbleActivity.duoBubbleMsgSamples()
            val contentUri =
                "https://android.example.com/chat/yourChatId".toUri()
            val bubbleIntent = PendingIntent.getActivity(
                activity,
                60,
                Intent(activity, DuoBubbleActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
                    .setData(contentUri),
                activity.flagUpdateCurrent()
            )
            bubble = NotificationCompat.BubbleMetadata.Builder(bubbleIntent, contactIcon)
                .setDesiredHeight(500)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build()
            shortcut = ShortcutInfoCompat.Builder(activity, "contact_1")
                .setLocusId(LocusIdCompat("contact_1"))
                .setLongLived(true)
                .setIntent(
                    Intent(activity, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUri)
                )
                .setShortLabel(contact.name!!)
                .setIcon(contactIcon)
                .setPerson(contact)
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

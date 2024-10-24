package com.vanskarner.samplenotify.styles

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.net.toUri
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.samplenotify.BasicBubbleActivity
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.NotifyMessaging
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.SimpleNotify

class MessagingActivity : BaseActivity() {
    companion object {
        const val TYPE = "Messaging"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //If the permit request is rejected twice, the remaining attempts will be false.
            findViewById<TextView>(R.id.tvPermission).text = getPermissionMsg(isGranted)
            if (!isGranted) showPermissionDeniedDialog()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messaging_style_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = getPermissionMsg(hasNotificationPermission())
            findViewById<TextView>(R.id.tvPermission).text = permission
            findViewById<Button>(R.id.btnPermission).setOnClickListener {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        findViewById<Button>(R.id.btnSetting).setOnClickListener { }
        findViewById<Button>(R.id.btnType1).setOnClickListener { individualConversation1() }
        findViewById<Button>(R.id.btnType2).setOnClickListener { individualConversation2() }
        findViewById<Button>(R.id.btnType3).setOnClickListener { groupConversation() }
        findViewById<Button>(R.id.btnType4).setOnClickListener { bubbleConversation() }
    }

    private fun bubbleConversation() {
        SimpleNotify.with(this)
            .asDuoMessaging {
                you = Person.Builder()
                    .setName("You")
                    .setIcon(iconFromAssets("sample_avatar.jpg"))
                    .build()
                contact = Person.Builder()
                    .setName("Admiral of Charco")
                    .setIcon(iconFromAssets("almirante_charco.jpg"))
                    .build()
                messages = BasicBubbleActivity.bubbleMessageSamples()
                val contentUri =
                    "https://android.example.com/chat/yourChatId".toUri()
                val bubbleIntent = PendingIntent.getActivity(
                    this@MessagingActivity,
                    2,//requestCode:Int
                    Intent(
                        this@MessagingActivity,
                        BasicBubbleActivity::class.java
                    )//cls:Class<Activity>
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUri),
                    flagUpdateCurrent()
                )
                bubble = Pair(
                    first = NotificationCompat.BubbleMetadata.Builder(
                        bubbleIntent,
                        iconFromAssets("almirante_charco.jpg")
                    )//groupIcon:IconCompact
                        .setDesiredHeight(500)
                        .setAutoExpandBubble(true)//*
                        .setSuppressNotification(true)//*
                        .build(),
                    second = ShortcutInfoCompat.Builder(
                        this@MessagingActivity,
                        "group_123"
                    )//groupId:String
                        .setLongLived(true)
                        .setIntent(
                            Intent(
                                this@MessagingActivity,
                                MainActivity::class.java
                            )//cls:Class<Activity>
                                .setAction(Intent.ACTION_VIEW)
                                .setData(contentUri)
                        )
                        .setShortLabel("Admiral of Charco")
                        .setIcon(iconFromAssets("almirante_charco.jpg"))
                        .build()
                )
                ShortcutManagerCompat.pushDynamicShortcut(this@MessagingActivity, bubble!!.second)
            }
            .show()
    }

    private fun groupConversation(notifyId: Int = 30) {
        SimpleNotify.with(this)
            .asGroupMessaging {
                id = notifyId
                conversationTitle = "The Big Show"
                you = Person.Builder()
                    .setName("You")
                    .setIcon(iconFromAssets("dina1.jpg"))
                    .build()
                messages = arrayListOf(
                    NotifyMessaging.YourMsg(
                        "Help me find my rolex...",
                        System.currentTimeMillis() - (3 * 60 * 1000)
                    ),
                    NotifyMessaging.ContactMsg(
                        "I'm sorry, but I already supported her by making Harvey Colchado fall.",
                        System.currentTimeMillis() - (1 * 60 * 1000),
                        Person.Builder().setName("Ministroll")
                            .setIcon(iconFromAssets("ministroll.jpg")).build()
                    ),
                    NotifyMessaging.ContactMsg(
                        "Hey don't bother me, I supported it by unsubscribing people in December 2022.",
                        System.currentTimeMillis(),
                        Person.Builder().setName("Carnicero")
                            .setIcon(iconFromAssets("carnicero.jpg")).build()
                    )
                )
            }
            .addReplyAction {
                label = "Respond"
                replyPending = getReplyPendingIntent(notifyId, TYPE)
                remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("Response").build()
            }
            .addAction {
                label = "Mute"
                pending = getSimplePendingIntent(MessagingActivity::class.java)
            }
            .show()
    }

    private fun individualConversation1(notifyId: Int = 10) {
        SimpleNotify.with(this)
            .asDuoMessaging {
                id = notifyId
                you = Person.Builder()
                    .setName("You")
                    .setIcon(iconFromAssets("dina1.jpg"))
                    .build()
                contact = Person.Builder()
                    .setName("Ministroll")
                    .setIcon(iconFromAssets("ministroll.jpg"))
                    .build()
                messages = arrayListOf(
                    NotifyMessaging.YourMsg(
                        "Help me find my rolex...",
                        System.currentTimeMillis() - (3 * 60 * 1000)
                    ),
                    NotifyMessaging.ContactMsg(
                        "I'm sorry, but I already supported her by making Harvey Colchado fall.",
                        System.currentTimeMillis() - (1 * 60 * 1000)
                    ),
                    NotifyMessaging.YourMsg(
                        "Only for this detail you are Minister, otherwise it would be a different story.",
                        System.currentTimeMillis()
                    )
                )
            }
            .addReplyAction {
                label = "Respond"
                replyPending = getReplyPendingIntent(notifyId, TYPE)
                remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
            }
            .addAction {
                label = "Mute"
                pending = getSimplePendingIntent(MessagingActivity::class.java)
            }
            .show()
    }

    private fun individualConversation2(notifyId: Int = 20) {
        SimpleNotify.with(this)
            .asDuoMessaging {
                id = notifyId
                you = Person.Builder()
                    .setName("You")
                    .setIcon(iconFromAssets("dina1.jpg"))
                    .build()
                contact = Person.Builder()
                    .setName("Ministroll")
                    .setIcon(iconFromAssets("ministroll.jpg"))
                    .build()
                messages = arrayListOf(
                    NotifyMessaging.YourMsg(
                        "Do you like my rolex?",
                        System.currentTimeMillis() - (3 * 60 * 1000)
                    ).setData(
                        "image/jpeg",
                        "content://com.vanskarner.samplenotify/photo/rolex_dina.jpg".toUri()
                    ),
                    NotifyMessaging.ContactMsg(
                        "Yes, my most excellent president... Don't you also want to use me as a mat? ",
                        System.currentTimeMillis() - (1 * 60 * 1000)
                    )
                )
            }
            .addReplyAction {
                label = "Respond"
                replyPending = getReplyPendingIntent(notifyId, TYPE)
                remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
            }
            .addAction {
                label = "Mute"
                pending = getSimplePendingIntent(MessagingActivity::class.java)
            }
            .show()
    }

}
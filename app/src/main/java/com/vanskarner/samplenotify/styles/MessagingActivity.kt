package com.vanskarner.samplenotify.styles

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.SimpleNotify
import com.vanskarner.samplenotify.bubbles.SampleBubbleNotificationView

class MessagingActivity : BaseActivity() {

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
        findViewById<Button>(R.id.btnType1).setOnClickListener { simple() }
        findViewById<Button>(R.id.btnType2).setOnClickListener {  }
        findViewById<Button>(R.id.btnType3).setOnClickListener { }
        findViewById<Button>(R.id.btnType4).setOnClickListener { }
        findViewById<Button>(R.id.btnType5).setOnClickListener {
            val notification = SampleBubbleNotificationView(this)
            notification.showNotification(
                title = "Any title",
                mySelf = Person.Builder()
                    .setName("Jennifer")
                    .setIcon(
                        IconCompat.createWithAdaptiveBitmap(
                            BitmapFactory.decodeStream(assets.open("sample_avatar.jpg"))
                        )
                    )
                    .setImportant(true)
                    .build(),
                message = Message(
                    "Look at my Rolex, loser...",
                    System.currentTimeMillis(),
                    Person.Builder()
                        .setName("Dina Balearte")
                        .setIcon(
                            IconCompat.createWithAdaptiveBitmap(
                                BitmapFactory.decodeStream(assets.open("dina1.jpg"))
                            )
                        )
                        .setImportant(true)
                        .build()
                ),
                notificationId = 123
            )
        }
    }

    private fun simple() {
        SimpleNotify.with(this)
            .asMessaging {
                user = Person.Builder()
                    .setName("Wayki")
                    .build()
                conversationTitle = "The Big Show"
                messages = arrayListOf(
                    Message(
                        "Help me find my rolex...",
                        System.currentTimeMillis() - (5 * 60 * 1000),
                        Person.Builder()
                            .setName("Balearte")
                            .setIcon(iconFromFilename("dina1.jpg"))
                            .build()
                    ),
                    Message(
                        "I'm sorry, but I already supported her by making Harvey Colchado fall.",
                        System.currentTimeMillis() - (5 * 60 * 1000),
                        Person.Builder()
                            .setName("Ministroll")
                            .setIcon(iconFromFilename("ministroll.jpg"))
                            .build()
                    ),
                    Message(
                        "Hey don't bother me, I supported it by unsubscribing people in December 2022.",
                        System.currentTimeMillis() - (5 * 60 * 1000),
                        Person.Builder()
                            .setName("Carnicero")
                            .setIcon(iconFromFilename("carnicero.jpg"))
                            .build()
                    )
                )
            }
            .show()
    }

    private fun iconFromFilename(fileName: String): IconCompat =
        IconCompat.createWithAdaptiveBitmap(BitmapFactory.decodeStream(assets.open(fileName)))

}
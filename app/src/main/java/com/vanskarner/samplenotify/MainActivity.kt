package com.vanskarner.samplenotify

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.vanskarner.samplenotify.bubbles.SampleBubbleNotificationView
import com.vanskarner.samplenotify.styles.BasicActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.btnBasic).setOnClickListener { goToActivity(BasicActivity::class.java) }
        findViewById<Button>(R.id.btnBigText).setOnClickListener { }
        findViewById<Button>(R.id.btnBigPicture).setOnClickListener { }
        findViewById<Button>(R.id.btnInbox).setOnClickListener { }
        findViewById<Button>(R.id.btnMessaging).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
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
    }

}
package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.vanskarner.samplenotify.types.call.EXTRA_CALL_TYPE
import com.vanskarner.simplenotify.SimpleNotify

class CallNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_HANG_UP" -> {
                stopSelf()
                return START_NOT_STICKY
            }
        }
        val callType = intent?.extras?.getString(EXTRA_CALL_TYPE) ?: "nothing"
        val notificationBuilder = when (callType) {
            "incoming" -> SimpleNotify.with(this)
                .asCall {
                    type = "incoming"
                    val image = BitmapFactory.decodeStream(assets.open("dina1.jpg"))
                    caller = Person.Builder()
                        .setName("Dina Balearte")
                        .setIcon(IconCompat.createWithBitmap(image))
                        .build()
                    answer = pendingIntentionHangupCall()
                    declineOrHangup = pendingIntentionHangupCall()
                }
                .addAction {
                    icon = R.drawable.baseline_video_camera_front_24
                    title = "Screen Call"
                    pending = pendingIntentionHangupCall()
                }
                .generateBuilder()

            "ongoing" -> SimpleNotify.with(this)
                .asCall {
                    type = "ongoing"
                    val image = BitmapFactory.decodeStream(assets.open("ministroll.jpg"))
                    caller = Person.Builder()
                        .setName("Ministroll")
                        .setIcon(IconCompat.createWithBitmap(image))
                        .build()
                    declineOrHangup = pendingIntentionHangupCall()
                }
                .generateBuilder()

            "screening" -> SimpleNotify.with(this)
                .asCall {
                    type = "screening"
                    val image = BitmapFactory.decodeStream(assets.open("morgan_rata.jpg"))
                    caller = Person.Builder()
                        .setName("Morgan Rata")
                        .setIcon(IconCompat.createWithBitmap(image))
                        .build()
                    answer = pendingIntentionHangupCall()
                    declineOrHangup = pendingIntentionHangupCall()
                }
                .generateBuilder()

            else -> null
        }
        notificationBuilder?.let { startForeground(156, it.build()) }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun pendingIntentionHangupCall(): PendingIntent {
        return PendingIntent.getService(
            this,
            4,
            Intent(this, CallNotificationService::class.java)
                .apply { action = "ACTION_HANG_UP" },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}
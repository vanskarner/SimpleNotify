package com.vanskarner.samplenotify

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val REMOTE_INPUT_KEY = "some_key"
        const val INTENT_EXTRA_NOTIFY_ID = "notificationId"
        const val INTENT_EXTRA_ACTIVITY = "ACTIVITY_TYPE"
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    fun getSimplePendingIntent(clazz: Class<out Activity>): PendingIntent {
        val intent = Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun pendingIntentToCloseNotification(notificationId: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            notificationId,
            Intent(this, NotificationDismissReceiver::class.java).apply {
                putExtra("notification_id", notificationId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun getReplyPendingIntent(extraNotifyId: Int, extraTypeActivity: String): PendingIntent {
        println("notificationId2 -> $extraNotifyId | activityType2-> $extraTypeActivity")
        val replyIntent = Intent(this, RemoteInputBroadcast::class.java).apply {
            putExtra(INTENT_EXTRA_NOTIFY_ID, extraNotifyId)
            putExtra(INTENT_EXTRA_ACTIVITY, extraTypeActivity)

        }
        return PendingIntent.getBroadcast(
            this,
            123,
            replyIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications permission not granted")
            .setMessage("You have denied notifications permission. To enable it, go to the app settings.")
            .setPositiveButton("Go to Settings") { _, _ -> openSettings() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun iconFromAssets(fileName: String): IconCompat =
        IconCompat.createWithAdaptiveBitmap(BitmapFactory.decodeStream(assets.open(fileName)))

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun flagUpdateCurrent(mutable: Boolean = true): Int {
        return when {
            mutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE

            mutable -> PendingIntent.FLAG_UPDATE_CURRENT

            else -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

}
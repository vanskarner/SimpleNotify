package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val REMOTE_INPUT_KEY = "some_key"
        const val INTENT_EXTRA_NOTIFY_ID = "notificationId"
    }

    fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    fun getSimplePendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun getReplyPendingIntent(notifyId: Int): PendingIntent {
        val replyIntent = Intent(this, RemoteInputBroadcast::class.java).apply {
            putExtra(INTENT_EXTRA_NOTIFY_ID, notifyId)
        }
        return PendingIntent.getBroadcast(
            this,
            123,
            replyIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    fun goToActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications permission not granted")
            .setMessage("You have denied notifications permission. To enable it, go to the app settings.")
            .setPositiveButton("Go to Settings") { _, _ -> openSettings() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun getPermissionMsg(hasPermission: Boolean): String =
        if (hasPermission) "Granted" else "Not Granted"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

}
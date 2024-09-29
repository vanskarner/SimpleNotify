package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val REMOTE_INPUT_KEY = "some_key"
        const val INTENT_EXTRA_NOTIFY_ID = "notificationId"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //If the permit request is rejected twice, the remaining attempts will be false.
            showPermissionText(isGranted)
            if (!isGranted) showPermissionDeniedDialog()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showPermissionText(hasNotificationPermission())
            findViewById<Button>(R.id.btnPermission).setOnClickListener {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        findViewById<Button>(R.id.btnSetting).setOnClickListener { openSettings() }
        findViewById<Button>(R.id.btnType1).setOnClickListener { notifyBasic1() }
        findViewById<Button>(R.id.btnType2).setOnClickListener { notifyBasic2() }
        findViewById<Button>(R.id.btnType3).setOnClickListener { notifyBasic3() }
        findViewById<Button>(R.id.btnType4).setOnClickListener { notifyBasic4(this) }
        findViewById<Button>(R.id.btnType5).setOnClickListener { notifyBasic5(this) }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications permission not granted")
            .setMessage("You have denied notifications permission. To enable it, go to the app settings.")
            .setPositiveButton("Go to Settings") { _, _ -> openSettings() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun showPermissionText(hasPermission: Boolean) {
        val permission = if (hasPermission) "Granted" else "Not Granted"
        findViewById<TextView>(R.id.tvPermission).text = permission
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun notifyBasic1() {
        SimpleNotify.with(this)
            .asBasic {
                title = "Dina Basurearte: With her phrase “Your mom!"
                text = "A never-before-seen response from a female president to the people"
            }
            .show()
    }

    private fun notifyBasic2() {
        SimpleNotify.with(this)
            .asBasic {
                smallIcon = R.drawable.baseline_handshake_24
                title = "Dina Balearte: Order with bullets and promotions"
                text = "Promotions after repression, a touch of presidential irony."
                largeIcon = BitmapFactory.decodeResource(resources, R.drawable.dina4)
                pending = getSimplePendingIntent()
            }
            .show()
    }

    private fun notifyBasic3() {
        val notifyId = 666
        SimpleNotify.with(this)
            .asBasic {
                id = notifyId
                title = "Dina Corruptuarte: Waykis case in the shadows"
                text = "An alleged criminal network dedicated to influence peddling"
            }
            .addReplyAction {
                label = "Respond"
                replyPending = getReplyPendingIntent(notifyId)
                replyLabel = "response"
                replyKey = REMOTE_INPUT_KEY
            }
            .addAction {
                label = "Impeachment"
                pending = getSimplePendingIntent()
            }
            .addAction {
                label = "Report"
                pending = getSimplePendingIntent()
            }
            .show()
    }

    private fun notifyBasic4(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 20) {
                delay(1000)
                SimpleNotify.with(context)
                    .asBasic {
                        title = "Downloading Dina's Prosecutor File"
                        text = "Be careful their government and most of the police work together."
                    }
                    .progress(progress)
                    .hideProgress { progress == 100 }
                    .show()
            }
        }
    }

    private fun notifyBasic5(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 20) {
                delay(1000)
                SimpleNotify.with(context)
                    .asBasic {
                        title = "Downloading Dina's Prosecutor File"
                        text = "Be careful their government and most of the police work together."
                    }
                    .progress(progress, true)
                    .hideProgress { progress == 100 }
                    .show()
            }
        }
    }

    private fun getSimplePendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getReplyPendingIntent(notifyId: Int): PendingIntent {
        val replyIntent = Intent(this, RemoteInputBroadcast::class.java).apply {
            putExtra(INTENT_EXTRA_NOTIFY_ID, notifyId)
        }
        return PendingIntent.getBroadcast(
            applicationContext,
            123,
            replyIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }

}
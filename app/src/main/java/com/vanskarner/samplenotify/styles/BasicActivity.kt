package com.vanskarner.samplenotify.styles

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.RemoteInput
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.SimpleNotify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BasicActivity : BaseActivity() {
    companion object {
        const val TYPE = "Basic"
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            //If the permit request is rejected twice, the remaining attempts will be false.
            findViewById<TextView>(R.id.tvPermission).text = getPermissionMsg(isGranted)
            if (!isGranted) showPermissionDeniedDialog()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_style_fragment)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = getPermissionMsg(hasNotificationPermission())
            findViewById<TextView>(R.id.tvPermission).text = permission
            findViewById<Button>(R.id.btnPermission).setOnClickListener {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        findViewById<Button>(R.id.btnSetting).setOnClickListener { openSettings() }
        findViewById<Button>(R.id.btnType1).setOnClickListener { simple() }
        findViewById<Button>(R.id.btnType2).setOnClickListener { detail() }
        findViewById<Button>(R.id.btnType3).setOnClickListener { actions() }
        findViewById<Button>(R.id.btnType4).setOnClickListener { progress(this) }
        findViewById<Button>(R.id.btnType5).setOnClickListener { indeterminateProgress(this) }
    }

    private fun indeterminateProgress(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 20) {
                delay(1000)
                SimpleNotify.with(context)
                    .asBasic {
                        title = "Downloading Dina's Prosecutor File"
                        text = "Be careful their government and most of the police work together."
                    }
                    .progress {
                        indeterminate = true
                        hide = progress == 100
                    }
                    .show()
            }
        }
    }

    private fun progress(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 20) {
                delay(1000)
                SimpleNotify.with(context)
                    .asBasic {
                        title = "Downloading Dina's Prosecutor File"
                        text = "Be careful their government and most of the police work together."
                    }
                    .progress {
                        currentValue = progress
                        hide = progress == 100
                    }
                    .show()
            }
        }
    }

    private fun actions() {
        val notifyId = 666
        SimpleNotify.with(this)
            .asBasic {
                id = notifyId
                title = "Dina Corruptuarte: Waykis case in the shadows"
                text = "An alleged criminal network dedicated to influence peddling"
            }
            .addReplyAction {
                label = "Respond"
                replyPending = getReplyPendingIntent(notifyId, TYPE)
                remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
            }
            .addAction {
                label = "Impeachment"
                pending = getSimplePendingIntent(BasicActivity::class.java)
            }
            .addAction {
                label = "Report"
                pending = getSimplePendingIntent(BasicActivity::class.java)
            }
            .show()
    }

    private fun detail() {
        SimpleNotify.with(this)
            .asBasic {
                smallIcon = R.drawable.baseline_handshake_24
                title = "Dina Balearte: Order with bullets and promotions"
                text = "Promotions after repression, a touch of presidential irony."
                largeIcon = BitmapFactory.decodeStream(assets.open("dina2.jpg"))
                contentIntent = getSimplePendingIntent(BasicActivity::class.java)
            }
            .show()
    }

    private fun simple() {
        SimpleNotify.with(this)
            .asBasic {
                title = "Dina Basurearte: With her phrase “Your mom!"
                text = "A never-before-seen response from a female president to the people"
            }
            .show()
    }

}
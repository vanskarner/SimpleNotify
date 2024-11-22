package com.vanskarner.samplenotify.styles.call

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.Person
import com.vanskarner.samplenotify.BaseActivity
import com.vanskarner.samplenotify.CallNotificationService
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.SimpleNotify

class CallActivity : BaseActivity() {
    companion object {
        const val TYPE = "Call"
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
        setContentView(R.layout.call_style_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = getPermissionMsg(hasNotificationPermission())
            findViewById<TextView>(R.id.tvPermission).text = permission
            findViewById<Button>(R.id.btnPermission).setOnClickListener {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        findViewById<Button>(R.id.btnSetting).setOnClickListener { openSettings() }
        findViewById<Button>(R.id.btnType1).setOnClickListener {
//            simple()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //for API<= 31-32-33-34-35
                val serviceIntent = Intent(this, CallNotificationService::class.java)
                startService(serviceIntent)
            } else {
                //for API>= 30
                simple()
            }
        }
    }

    private fun simple() {
        SimpleNotify.with(this)
            .asCall {
                type = "incoming"
//                caller = Person.Builder().setName("Juan").build()
//                answer = null
//                declineOrHangup = null
            }
            .show()
    }

}
package com.vanskarner.samplenotify

import android.os.Bundle
import android.widget.Button
import com.vanskarner.samplenotify.styles.basic.BasicActivity
import com.vanskarner.samplenotify.styles.call.CallActivity
import com.vanskarner.samplenotify.styles.messaging.MessagingActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViewById<Button>(R.id.btnBasic).setOnClickListener { goToActivity(BasicActivity::class.java) }
        findViewById<Button>(R.id.btnBigText).setOnClickListener { }
        findViewById<Button>(R.id.btnBigPicture).setOnClickListener { }
        findViewById<Button>(R.id.btnInbox).setOnClickListener { }
        findViewById<Button>(R.id.btnMessaging).setOnClickListener { goToActivity(MessagingActivity::class.java) }
        findViewById<Button>(R.id.btnCall).setOnClickListener { goToActivity(CallActivity::class.java) }
    }

}
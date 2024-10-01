package com.vanskarner.samplenotify

import android.os.Bundle
import android.widget.Button
import com.vanskarner.samplenotify.styles.BasicActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.btnBasic).setOnClickListener { goToActivity(BasicActivity::class.java) }
        findViewById<Button>(R.id.btnBigText).setOnClickListener { }
        findViewById<Button>(R.id.btnBigPicture).setOnClickListener { }
        findViewById<Button>(R.id.btnInbox).setOnClickListener { }
        findViewById<Button>(R.id.btnMessaging).setOnClickListener { }
    }

}
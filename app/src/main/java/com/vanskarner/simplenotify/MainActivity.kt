package com.vanskarner.simplenotify

import android.app.Activity
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.btnType1).setOnClickListener { notificationType1() }
        findViewById<Button>(R.id.btnType2).setOnClickListener { notificationType2() }
        findViewById<Button>(R.id.btnType3).setOnClickListener { notificationType3() }
        findViewById<Button>(R.id.btnType4).setOnClickListener { notificationType4() }
    }

    private fun notificationType1() {

    }

    private fun notificationType2() {

    }

    private fun notificationType3() {

    }

    private fun notificationType4() {

    }
}
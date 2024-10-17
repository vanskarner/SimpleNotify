package com.vanskarner.samplenotify

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BasicBubbleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_bubble_activity)
        val msg = intent.data?.lastPathSegment ?: return
        val bitmapImg = BitmapFactory.decodeStream(assets.open("rolex_dina.jpg"))
        findViewById<TextView>(R.id.tvMessage).text = msg
        findViewById<ImageView>(R.id.imvImg).setImageBitmap(bitmapImg)
    }

}

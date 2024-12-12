package com.vanskarner.samplenotify.types.stacking

import android.graphics.BitmapFactory
import android.widget.ArrayAdapter
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.databinding.MainActivityBinding
import com.vanskarner.simplenotify.SimpleNotify

fun showStacking(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Stacking with different styles" to ::withDifferentStyles
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun withDifferentStyles(activity: MainActivity) {
    showBasicType(activity)
    showBigPictureType(activity)
    showBigTextType(activity)
    stacking(activity)
}

private fun showBasicType(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBasic {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
        }
        .extras {
            groupKey = "Samples"
        }
        .show()
}

private fun showBigPictureType(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigPicture {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            summaryText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
        }
        .extras {
            groupKey = "Samples"
        }
        .show()
}

private fun showBigTextType(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigText {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            bigText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
        }
        .extras {
            groupKey = "Samples"
        }
        .show()
}

private fun stacking(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asInbox {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            lines = arrayListOf("My item One", "My item Second", "My item Third", "My item Four")
        }
        .extras {
            groupKey = "Samples"
        }
        .stackable {
            smallIcon = R.drawable.baseline_handshake_24
            title = "My Summary"
            summaryText = "My Group"
        }
        .show()
}
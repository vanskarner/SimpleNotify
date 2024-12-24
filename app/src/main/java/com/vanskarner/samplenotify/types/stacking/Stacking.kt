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
            title = "Dina Balearte: Order with bullets and promotions"
            text = "Promotions after repression, a touch of presidential irony."
        }
        .extras {
            groupKey = "Samples"
        }
        .show()
}

private fun showBigPictureType(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigPicture {
            title = "Dina Miseriarte romanticizes poverty."
            text = "Praises cooking with S/10"
            summaryText =
                "Dina celebrates cooking with S/10, while in Palacio they spend S/4000 a day on food."
            image = BitmapFactory.decodeStream(activity.assets.open("dina_10soles.jpg"))
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
            text = "Dina responds to citizen with: your mom!"
            bigText =
                "Peru's President Dina Boluarte replied \"your mom\" to a citizen who called her \"corrupt\" during the 203rd independence anniversary parade. She remained smiling, raised her hand, and continued thanking the audience despite the incident."
        }
        .extras {
            groupKey = "Samples"
        }
        .show()
}

private fun stacking(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asInbox {
            title = "3 New mails from Dina"
            text = "3 new messages from the unpresentable"
            lines = arrayListOf(
                "Cover-up of fugitive Cerrón.",
                "Cover-up of fugitive Nicanor.",
                "Work to favor The Pact."
            )
        }
        .extras {
            groupKey = "Samples"
        }
        .stackable {
            smallIcon = R.drawable.baseline_handshake_24
            title = "The Corrupt Pact"
            summaryText = "Some Group"
        }
        .show()
}
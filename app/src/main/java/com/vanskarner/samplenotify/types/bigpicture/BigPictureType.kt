package com.vanskarner.samplenotify.types.bigpicture

import android.graphics.BitmapFactory
import android.widget.ArrayAdapter
import androidx.core.app.RemoteInput
import com.vanskarner.samplenotify.BaseActivity.Companion.REMOTE_INPUT_KEY
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.databinding.MainActivityBinding
import com.vanskarner.simplenotify.SimpleNotify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun showBigPictureTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Big Picture" to ::basic,
        "Big Picture with details" to ::withDetails,
        "Big Picture with actions" to ::withActions,
        "Big Picture with progress" to ::withProgress,
        "Big Picture with indeterminate progress" to ::withIndeterminateProgress
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigPicture {
            title = "Dina Balearte's Rolex watches"
            text = "Luxury watches in loan quality."
            summaryText =
                "Passive bribery punishes officials who accept bribes with imprisonment and disqualification."
            image = BitmapFactory.decodeStream(activity.assets.open("dina_rolex.jpg"))
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigPicture {
            id = 20
            tag = "BIG_PICTURE_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina1.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 7000L
            title = "Dina Miseriarte romanticizes poverty."
            text = "Praises cooking with S/10"
            summaryText =
                "Dina celebrates cooking with S/10, while in Palacio they spend S/4000 a day on food."
            image = BitmapFactory.decodeStream(activity.assets.open("dina_10soles.jpg"))
            subText = "Trends"
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 21
    SimpleNotify.with(activity)
        .asBigPicture {
            id = notifyId
            title = "No progress in the investigation of deaths in Dina's government."
            text = "Organizations condemn lack of progress."
            summaryText =
                "Families of the deceased from Dec 2022 to Mar 2023 seek justice and accountability."
            image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
        }
        .addReplyAction {
            title = "Respond"
            replyPending = activity.pendingIntentToCloseNotification(notifyId)
            remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
        }
        .addAction {
            title = "Impeachment"
            pending = activity.pendingIntentToCloseNotification(notifyId)
        }
        .addAction {
            title = "Report"
            pending = activity.pendingIntentToCloseNotification(notifyId)
        }
        .show()
}

private fun withProgress(activity: MainActivity) {
    CoroutineScope(Dispatchers.IO).launch {
        for (progress in 0..100 step 20) {
            delay(1000)
            SimpleNotify.with(activity)
                .asBigPicture {
                    title = "Dina Boluarte's case file"
                    text =
                        if (progress < 100) "Downloading investigations..." else "Investigations downloaded."
                    summaryText = if (progress < 100) "${100 - progress} seconds left"
                    else "Downloaded files. It's time to do something."
                    image = BitmapFactory.decodeStream(activity.assets.open("dina_files.jpg"))
                    subText = if (progress < 100) "${progress}%" else "Complete download"
                }
                .progress {
                    currentValue = progress
                    hide = progress == 100
                }
                .show()
        }
    }
}

private fun withIndeterminateProgress(activity: MainActivity) {
    CoroutineScope(Dispatchers.IO).launch {
        for (progress in 0..100 step 20) {
            delay(1000)
            SimpleNotify.with(activity)
                .asBigPicture {
                    title = "Dina Boluarte's case file"
                    text =
                        if (progress < 100) "Downloading investigations..." else "Investigations downloaded."
                    summaryText =
                        if (progress < 100) "Please wait..." else "Downloaded files. It's time to do something."
                    image = BitmapFactory.decodeStream(activity.assets.open("dina_files.jpg"))
                    subText = if (progress < 100) "Downloading..." else "Complete download"
                }
                .progress {
                    indeterminate = true
                    hide = progress == 100
                }
                .show()
        }
    }
}
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
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            summaryText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigPicture {
            id = 20
            tag = "BIG_PICTURE_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 5000L
            title = "Dina Balearte: Order with bullets and promotions"
            text = "Promotions after repression, a touch of presidential irony."
            summaryText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            subText = "Any SubText"
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 21
    SimpleNotify.with(activity)
        .asBigPicture {
            id = notifyId
            title = "Dina Corruptuarte: Waykis case in the shadows"
            text = "An alleged criminal network dedicated to influence peddling"
            summaryText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
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
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    summaryText = if (progress < 100) "${100-progress} seconds left"
                    else "Your request has been processed successfully"
                    image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
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
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    summaryText = if (progress < 100) "Please wait..."
                    else "Your request has been processed successfully"
                    image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
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
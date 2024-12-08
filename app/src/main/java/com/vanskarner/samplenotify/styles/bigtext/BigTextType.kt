package com.vanskarner.samplenotify.styles.bigtext

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

const val BIG_TEXT_TYPE = "BIG_TEXT_TYPE"

fun showBigTextTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Big Text" to ::basic,
        "Big Text with details" to ::withDetails,
        "Big Text with actions" to ::withActions,
        "Big Text with progress" to ::withProgress,
        "Big Text with indeterminate progress" to ::withIndeterminateProgress
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigText {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            bigText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            summaryText = "Summary Text"
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBigText {
            id = 30
            tag = "BIG_TEXT_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 5000L
            title = "Dina Balearte: Order with bullets and promotions"
            text = "Promotions after repression, a touch of presidential irony."
            bigText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            summaryText = "Summary Text"
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 31
    SimpleNotify.with(activity)
        .asBigText {
            id = notifyId
            title = "Dina Corruptuarte: Waykis case in the shadows"
            text = "An alleged criminal network dedicated to influence peddling"
            bigText =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            summaryText = "Summary Text"
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
                .asBigText {
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    bigText =
                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"
                    summaryText = if (progress < 100) "Downloading($progress%)..."
                    else "Download finished"
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
                .asBigText {
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    bigText =
                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"
                    summaryText = if (progress < 100) "Downloading($progress%)..."
                    else "Download finished"
                }
                .progress {
                    indeterminate = true
                    hide = progress == 100
                }
                .show()
        }
    }
}

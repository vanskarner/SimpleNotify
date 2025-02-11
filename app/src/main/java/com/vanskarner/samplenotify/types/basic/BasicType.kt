package com.vanskarner.samplenotify.types.basic

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

fun showBasicTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Basic" to ::basic,
        "Basic with details" to ::withDetails,
        "Basic with actions" to ::withActions,
        "Basic with progress" to ::withProgress,
        "Basic with undetermined progress" to ::withIndeterminateProgress
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBasic {
            title = "Peru: Corrupt pact and millionaire arms purchases"
            text = "Emergencies in health, housing and education are left aside"
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asBasic {
            id = 10
            tag = "BASIC_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 5000L
            title = "Dina Balearte: Order with bullets and promotions"
            text = "Promotions after repression, a touch of presidential irony."
            subText = "Trends"
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 11
    SimpleNotify.with(activity)
        .asBasic {
            id = notifyId
            title = "Dina Corruptuarte: Waykis case in the shadows"
            text = "An alleged criminal network dedicated to influence peddling"
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
                .asBasic {
                    smallIcon = R.drawable.baseline_file_download_24
                    title = if (progress < 100) "Downloading Dina's File" else "Dina Files Available"
                    subText = if (progress < 100) "${progress}%" else "Complete download"
                    text = if (progress < 100) "${100 - progress} seconds left"
                    else "Investigations downloaded successfully. Be careful."
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
                .asBasic {
                    smallIcon = R.drawable.baseline_file_download_24
                    title = if (progress < 100) "Downloading Dina's File" else "Dina Files Available"
                    subText = if (progress < 100) "Downloading..." else "Complete download"
                    text = if (progress < 100) "Please wait..."
                    else "Investigations downloaded successfully. Be careful."
                }
                .progress {
                    indeterminate = true
                    hide = progress == 100
                }
                .show()
        }
    }
}
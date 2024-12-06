package com.vanskarner.samplenotify.styles.inbox

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

const val INBOX_TYPE = "INBOX_TYPE"

fun showInboxTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Inbox" to ::basic,
        "Inbox with details" to ::withDetails,
        "Inbox with actions" to ::withActions,
        "Inbox with progress" to ::withProgress,
        "Inbox with indeterminate Progress" to ::withIndeterminateProgress
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asInbox {
            title = "Dina Basurearte: With her phrase “Your mom!"
            text = "A never-before-seen response from a female president to the people"
            lines = arrayListOf("My item One", "My item Second", "My item Third", "My item Four")
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asInbox {
            id = 80
            tag = "INBOX_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 2500L
            autoCancel = true
            title = "Dina Balearte: Order with bullets and promotions"
            text = "Promotions after repression, a touch of presidential irony."
            lines = arrayListOf("My item One", "My item Second", "My item Third", "My item Four")
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 81
    SimpleNotify.with(activity)
        .asInbox {
            id = notifyId
            title = "Dina Corruptuarte: Waykis case in the shadows"
            text = "An alleged criminal network dedicated to influence peddling"
            lines = arrayListOf("My item One", "My item Second", "My item Third", "My item Four")
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
                .asInbox {
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    lines = arrayListOf(
                        "My item One",
                        "My item Second",
                        "My item Third",
                        "My item Four"
                    )
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
                .asInbox {
                    title = "Dina Basurearte: With her phrase “Your mom!"
                    text = "A never-before-seen response from a female president to the people"
                    lines = arrayListOf(
                        "My item One",
                        "My item Second",
                        "My item Third",
                        "My item Four"
                    )
                }
                .progress {
                    indeterminate = true
                    hide = progress == 100
                }
                .show()
        }
    }
}
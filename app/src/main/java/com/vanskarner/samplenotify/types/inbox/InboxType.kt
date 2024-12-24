package com.vanskarner.samplenotify.types.inbox

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
            title = "3 New mails from Dina"
            text = "3 new messages from the unpresentable"
            lines = arrayListOf(
                "Cover-up of fugitive Cerrón.",
                "Cover-up of fugitive Nicanor.",
                "Work to favor The Pact."
            )
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asInbox {
            id = 80
            tag = "INBOX_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina1.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 5000L
            title = "3 New mails from Dina"
            text = "3 new messages from the unpresentable"
            lines = arrayListOf(
                "Cover-up of fugitive Cerrón.",
                "Cover-up of fugitive Nicanor.",
                "Work to favor The Pact."
            )
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 81
    SimpleNotify.with(activity)
        .asInbox {
            id = notifyId
            title = "3 New mails from Dina"
            text = "3 new messages from the unpresentable"
            lines = arrayListOf(
                "Cover-up of fugitive Cerrón.",
                "Cover-up of fugitive Nicanor.",
                "Work to favor The Pact."
            )
        }
        .addReplyAction {
            title = "Respond"
            replyPending = activity.pendingIntentToCloseNotification(notifyId)
            remote = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("response").build()
        }
        .addAction {
            title = "Archive"
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
                    title = "100 New mails from The Pact"
                    subText = if (progress < 100) "${progress}%" else "Messages loaded"
                    text = "New messages for the 2025 plan"
                    lines = if (progress < 100) arrayListOf() else arrayListOf(
                        "Other 96 messages...",
                        "Continue favoring crime to keep the population busy.",
                        "Capture the voting system.",
                        "Deactivate research units.",
                        "Protecting Congressmen."
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
                    title = "100 New mails from The Pact"
                    subText = if (progress < 100) "Waiting messages..." else "Messages loaded"
                    text = "New messages for the 2025 plan"
                    lines = if (progress < 100) arrayListOf() else arrayListOf(
                        "Other 96 messages...",
                        "Continue favoring crime to keep the population busy.",
                        "Capture the voting system.",
                        "Deactivate research units.",
                        "Protecting Congressmen."
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
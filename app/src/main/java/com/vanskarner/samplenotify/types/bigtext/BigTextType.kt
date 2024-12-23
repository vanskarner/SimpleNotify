package com.vanskarner.samplenotify.types.bigtext

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
            text = "Dina responds to citizen with: your mom!"
            bigText =
                "Peru's President Dina Boluarte replied \"your mom\" to a citizen who called her \"corrupt\" during the 203rd independence anniversary parade. She remained smiling, raised her hand, and continued thanking the audience despite the incident."
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
                "Despite denying under oath any direct contact with commanders, President Boluarte met with military and police chiefs during protests, praised security forces, vilified protesters without proof, and promoted officials tied to deadly operations instead of demanding accountability."
            subText = "Trends"
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
                "The \"Los Waykis in the shadows\" case has drawn significant attention in Peru. Judge Richard Concepción Carhuancho ordered 36 months of preventive detention for Nicanor Boluarte, the president’s brother, for alleged ties to a criminal group influencing political appointments."
            subText = "Summary Text"
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
                    title = "Downloading Dina's File"
                    text = "5 Investigations in less than 2 years."
                    bigText = if (progress < 100) "${100 - progress} seconds left, please wait..."
                    else "Investigations downloaded: These include charges of abandonment of office, alleged corruption, deaths and massacres during protests, cover-up of a fugitive, and secret agreements with former National Prosecutor 'Vane'"
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
                .asBigText {
                    title = "Downloading Dina's File"
                    text = "5 Investigations in less than 2 years."
                    bigText =
                        if (progress < 100) "Please wait while your request is being processed..."
                        else "Investigations downloaded: These include charges of abandonment of office, alleged corruption, deaths and massacres during protests, cover-up of a fugitive, and secret agreements with former National Prosecutor 'Vane'"
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

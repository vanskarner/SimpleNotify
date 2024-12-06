package com.vanskarner.samplenotify.styles.customdesign

import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import androidx.core.app.RemoteInput
import com.vanskarner.samplenotify.BaseActivity.Companion.REMOTE_INPUT_KEY
import com.vanskarner.samplenotify.MainActivity
import com.vanskarner.samplenotify.R
import com.vanskarner.samplenotify.databinding.MainActivityBinding
import com.vanskarner.simplenotify.SimpleNotify
import java.util.Locale

const val CUSTOM_DESIGN_TYPE = "CUSTOM_DESIGN_TYPE"

fun showCustomDesignTypes(activity: MainActivity, binding: MainActivityBinding) {
    val options = mapOf(
        "Custom Design" to ::basic,
        "Custom Design with details" to ::withDetails,
        "Custom Design with actions" to ::withActions,
        "Custom Design | Counter" to ::counterType,
        "Custom Design | Counter without container" to ::counterWithoutContainer,
    )
    binding.gridView.adapter =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, options.keys.toList())
    binding.gridView.setOnItemClickListener { _, _, position, _ ->
        options.values.elementAt(position).invoke(activity)
    }
}

private fun basic(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asCustomDesign {
            smallRemoteViews = {
                RemoteViews(activity.packageName, R.layout.small_notification_1)
            }
            largeRemoteViews = {
                RemoteViews(activity.packageName, R.layout.large_notification_1)
            }
        }
        .show()
}

private fun withDetails(activity: MainActivity) {
    SimpleNotify.with(activity)
        .asCustomDesign {
            id = 50
            tag = "CUSTOM_DESIGN_TAG"
            smallIcon = R.drawable.baseline_handshake_24
            largeIcon = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
            contentIntent = activity.pendingIntentToCloseNotification(id ?: 0)
            timeoutAfter = 2500L
            autoCancel = true
            smallRemoteViews = {
                RemoteViews(activity.packageName, R.layout.small_notification_1)
            }
            largeRemoteViews = {
                RemoteViews(activity.packageName, R.layout.large_notification_1)
            }
        }
        .show()
}

private fun withActions(activity: MainActivity) {
    val notifyId = 51
    SimpleNotify.with(activity)
        .asCustomDesign {
            id = notifyId
            smallRemoteViews = {
                RemoteViews(activity.packageName, R.layout.small_notification_1)
            }
            largeRemoteViews = {
                RemoteViews(activity.packageName, R.layout.large_notification_1)
            }
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

private fun counterType(activity: MainActivity) {
    startNotificationCounter(activity, 52, true)
}

private fun counterWithoutContainer(activity: MainActivity) {
    startNotificationCounter(activity, 53, false)
}

private fun startNotificationCounter(
    activity: MainActivity,
    notifyId: Int,
    withContainer: Boolean
) {
    val countDownTimer = object : CountDownTimer(20000L, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = (millisUntilFinished / 1000).toInt()
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60
            val format = "%02d:%02d:%02d"
            val time = String.format(Locale.US, format, hours, minutes, remainingSeconds)
            counterTypeNotification(activity, notifyId, time, withContainer)
        }

        override fun onFinish() {
            counterTypeNotification(activity, notifyId, "00:00:00", withContainer)
        }
    }
    countDownTimer.start()
}

private fun counterTypeNotification(
    activity: MainActivity,
    notifyId: Int,
    time: String,
    withContainer: Boolean
) {
    SimpleNotify.with(activity)
        .asCustomDesign {
            id = notifyId
            hasStyle = withContainer
            smallRemoteViews = {
                val smallView =
                    RemoteViews(activity.packageName, R.layout.small_notification_2)
                val titleMsg = activity.getString(R.string.custom_notification_msg_2)
                smallView.setTextViewText(R.id.notification_timer, time)
                smallView.setTextViewText(R.id.notification_title, titleMsg)
                smallView
            }
            largeRemoteViews = {
                val largeView =
                    RemoteViews(activity.packageName, R.layout.large_notification_2)
                val image = BitmapFactory.decodeStream(activity.assets.open("dina2.jpg"))
                val titleMsg = activity.getString(R.string.custom_notification_msg_3)
                largeView.setTextViewText(R.id.notification_timer, time)
                largeView.setTextViewText(R.id.notification_title, titleMsg)
                largeView.setImageViewBitmap(R.id.notification_imv, image)
                largeView
            }
        }
        .show()
}
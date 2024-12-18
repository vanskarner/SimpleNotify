package com.vanskarner.simplenotify.internal.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.internal.ConfigData
import com.vanskarner.simplenotify.internal.Notify

internal class CallNotify(
    private val context: Context, private val configData: ConfigData
) : Notify, BaseNotify(
    context,
    configData.progressData,
    configData.extras,
    configData.stackableData,
    configData.channelId,
    configData.actions
) {
    companion object {
        private const val INCOMING = "incoming"
        private const val ONGOING = "ongoing"
        private const val SCREENING = "screening"
    }

    private val data = configData.data as Data.CallData

    override fun show(): Pair<Int, Int> {
        if (isDataInvalid()) return invalidNotificationResult()
        return notify(data)
    }

    override fun generateBuilder(): NotificationCompat.Builder? {
        if (isDataInvalid()) return null
        return createNotification(data, selectChannelId())
    }

    override fun applyData(builder: NotificationCompat.Builder) {
        val caller = data.caller ?: Data.CallData.defaultCaller(context)
        val secondCaller = Data.CallData.defaultSecondCaller(context)
        val notificationSettings: (NotificationCompat.CallStyle) -> Unit = { style ->
            style.setVerificationText(data.verificationText)
                .setVerificationIcon(data.verificationIcon)
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setVibrate(longArrayOf(0, 500, 1000, 500))
                .setStyle(style)
                .addPerson(secondCaller)
        }
        when (data.type.lowercase()) {
            INCOMING -> {
                val answerIntent = data.answer
                val declineIntent = data.declineOrHangup
                if (answerIntent != null && declineIntent != null) {
                    val style = NotificationCompat.CallStyle
                        .forIncomingCall(caller, declineIntent, answerIntent)
                    notificationSettings(style)
                }
            }

            ONGOING -> {
                data.declineOrHangup?.let {
                    val style = NotificationCompat.CallStyle.forOngoingCall(caller, it)
                    notificationSettings(style)
                }
            }

            SCREENING -> {
                val hangUpIntent = data.declineOrHangup
                val answerIntent = data.answer
                if (hangUpIntent != null && answerIntent != null) {
                    val style = NotificationCompat.CallStyle
                        .forScreeningCall(caller, hangUpIntent, answerIntent)
                    notificationSettings(style)
                }
            }
        }
    }

    override fun enableProgress(): Boolean = false

    override fun selectChannelId(): String {
        return when {
            notifyChannel.checkChannelNotExists(context, configData.channelId) ->
                notifyChannel.applyCallChannel(context)

            else -> configData.channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    private fun isDataInvalid(): Boolean {
        val requiredFields = when (data.type.lowercase()) {
            INCOMING, SCREENING -> listOf(data.answer, data.declineOrHangup)
            ONGOING -> listOf(data.declineOrHangup)
            else -> return true
        }
        return requiredFields.any { it == null }
    }

}
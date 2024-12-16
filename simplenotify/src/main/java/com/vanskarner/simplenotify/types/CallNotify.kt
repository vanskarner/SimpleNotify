package com.vanskarner.simplenotify.types

import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.Notify
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.StackableData

internal class CallNotify(
    private val context: Context,
    private val data: Data.CallData?,
    private val channelId: String?,
    extra: ExtraData,
    progressData: ProgressData?,
    stackableData: StackableData?,
    actions: Array<ActionData?>,
) : Notify, BaseNotify(context, progressData, extra, stackableData, actions) {

    override fun show(): Pair<Int, Int> {
        val requiredData = validateData() ?: return invalidNotificationResult()
        return notify(requiredData)
    }

    override fun generateBuilder(): NotificationCompat.Builder? {
        val myData = validateData() ?: return null
        return createNotification(myData, selectChannelId())
    }

    override fun applyData(builder: NotificationCompat.Builder) {
        val requiredData = data ?: return
        val caller = requiredData.caller ?: Data.CallData.defaultCaller(context)
        val secondCaller = Data.CallData.defaultSecondCaller(context)
        val notificationSettings: (NotificationCompat.CallStyle) -> Unit = { style ->
            style.setVerificationText(requiredData.verificationText)
                .setVerificationIcon(requiredData.verificationIcon)
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(longArrayOf(0, 500, 1000, 500))
                .setStyle(style)
                .addPerson(secondCaller)
        }
        val options = mapOf(
            "incoming" to {
                val answerIntent = data.answer
                val declineIntent = data.declineOrHangup
                if (answerIntent != null && declineIntent != null) {
                    val style = NotificationCompat.CallStyle
                        .forIncomingCall(caller, declineIntent, answerIntent)
                    notificationSettings(style)
                }
            }, "ongoing" to {
                data.declineOrHangup?.let {
                    val style = NotificationCompat.CallStyle.forOngoingCall(caller, it)
                    notificationSettings(style)
                }
            }, "screening" to {
                val hangUpIntent = data.declineOrHangup
                val answerIntent = data.answer
                if (hangUpIntent != null && answerIntent != null) {
                    val style = NotificationCompat.CallStyle
                        .forScreeningCall(caller, hangUpIntent, answerIntent)
                    notificationSettings(style)
                }
            })
        options[requiredData.type.lowercase()]?.invoke()
    }

    override fun selectChannelId(): String {
        return when {
            notifyChannel.checkChannelNotExists(context, channelId) -> notifyChannel.applyCallChannel(context)

            else -> channelId ?: notifyChannel.applyDefaultChannel(context)
        }
    }

    private fun validateData(): Data? {
        val myData = data ?: return null
        val requiredFields = when (myData.type.lowercase()) {
            "incoming", "screening" -> listOf(myData.answer, myData.declineOrHangup)
            "ongoing" -> listOf(myData.declineOrHangup)
            else -> return null
        }
        if (requiredFields.any { it == null }) return null
        return myData
    }

}
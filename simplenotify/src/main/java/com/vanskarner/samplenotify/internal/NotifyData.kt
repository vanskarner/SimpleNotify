package com.vanskarner.samplenotify.internal

import androidx.core.app.NotificationCompat.Builder
import android.content.Context
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data

@Suppress("ArrayInDataClass")
internal data class NotifyData(
    val context: Context,
    val data: Data,
    val progressData: ProgressData?,
    val channelData: ChannelData,
    val actions: Array<ActionData?>,
)

internal data class ProgressData(
    var currentPercentage: Int = 0,
    var indeterminate: Boolean = false,
    var conditionToHide: (() -> Boolean) = { false }
) {
    fun applyData(builder: Builder) {
        if (conditionToHide.invoke()) builder.setProgress(0, 0, false)
        else builder.setProgress(100, currentPercentage, indeterminate)
    }
}
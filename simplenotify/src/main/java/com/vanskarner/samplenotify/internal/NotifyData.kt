package com.vanskarner.samplenotify.internal

import android.content.Context
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.ChannelData
import com.vanskarner.samplenotify.Data

@Suppress("ArrayInDataClass")
internal data class NotifyData(
    val context: Context,
    val data: Data,
    val channelData: ChannelData,
    val actions: Array<ActionData?>,
)
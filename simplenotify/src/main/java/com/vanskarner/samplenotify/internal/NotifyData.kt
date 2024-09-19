package com.vanskarner.samplenotify.internal

import android.app.PendingIntent
import android.content.Context
import com.vanskarner.samplenotify.ActionData
import com.vanskarner.samplenotify.Data

@Suppress("ArrayInDataClass")
internal data class NotifyData<T : Data>(
    val context: Context,
    val data: T,
    val pending: PendingIntent,
    val actions: Array<ActionData?>
)
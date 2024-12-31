package com.vanskarner.simplenotify.internal

import com.vanskarner.simplenotify.ActionData
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.ExtraData
import com.vanskarner.simplenotify.ProgressData
import com.vanskarner.simplenotify.StackableData

internal data class ConfigData(
    val data: Data,
    val extras: ExtraData,
    val progressData: ProgressData?,
    val stackableData: StackableData?,
    val channelId: String?,
    val actions: List<ActionData?>
)
package com.vanskarner.samplenotify

import android.app.PendingIntent
import android.content.Context
import com.vanskarner.samplenotify.internal.BasicNotify
import com.vanskarner.samplenotify.internal.BigTextNotify
import com.vanskarner.samplenotify.internal.NotifyBase
import com.vanskarner.samplenotify.internal.PayLoadData

class NotifyConfig(private val context: Context) {
    private lateinit var data: Data
    private lateinit var pendingIntent: PendingIntent
    private val actions: Array<ActionData?> by lazy { arrayOfNulls(3) }


    fun asBasic(content: Data.BasicData.() -> Unit): NotifyConfig {
        data = Data.BasicData().apply(content)
        return this
    }

    fun asBigText(content: Data.BigTextData.() -> Unit): NotifyConfig {
        data = Data.BigTextData().apply(content)
        return this
    }

    fun click(pending: PendingIntent): NotifyConfig {
        pendingIntent = pending
        return this
    }

    fun addAction(action: ActionData.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData().apply(action)
        return this
    }

    fun show() = filter(data).show()

    private fun filter(data: Data): NotifyBase<*> {
        return when (data) {
            is Data.BasicData -> {
                BasicNotify(
                    PayLoadData(
                        context = context,
                        data = data,
                        pending = pendingIntent,
                        actions = actions
                    )
                )
            }

            is Data.BigTextData -> {
                BigTextNotify(
                    PayLoadData(
                        context = context,
                        data = data,
                        pending = pendingIntent,
                        actions = actions
                    )
                )
            }
        }
    }

}
package com.vanskarner.samplenotify

import android.content.Context
import com.vanskarner.samplenotify.internal.NotifyGenerator
import com.vanskarner.samplenotify.internal.NotifyData

class NotifyConfig(private val context: Context) {
    private var data: Data? = null
    private var channelData: ChannelData = ChannelData.byDefault(context)
    private val actions: Array<ActionData?> by lazy { arrayOfNulls(NotifyGenerator.MAXIMUM_ACTIONS) }
    private val notifyGenerator by lazy { NotifyGenerator }

    fun asBasic(content: Data.BasicData.() -> Unit): NotifyConfig {
        this.data = Data.BasicData().apply(content)
        return this
    }

    fun asBigText(content: Data.BigTextData.() -> Unit): NotifyConfig {
        this.data = Data.BigTextData().apply(content)
        return this
    }

    fun useChannel(content: ChannelData.() -> Unit): NotifyConfig {
        this.channelData = ChannelData().apply(content)
        return this
    }

    fun addAction(action: ActionData.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData().apply(action)
        return this
    }

    fun show() {
        data?.let {
            val notifyData = NotifyData(
                context = context,
                data = it,
                actions = actions,
                channelData = channelData
            )
            notifyGenerator.show(notifyData)
        }
    }

}
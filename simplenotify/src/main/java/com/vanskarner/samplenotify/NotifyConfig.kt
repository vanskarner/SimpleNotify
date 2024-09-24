package com.vanskarner.samplenotify

import android.content.Context
import com.vanskarner.samplenotify.internal.BasicNotify
import com.vanskarner.samplenotify.internal.BigTextNotify
import com.vanskarner.samplenotify.internal.Notify
import com.vanskarner.samplenotify.internal.NotifyData

class NotifyConfig(private val context: Context) {
    private var data: Data? = null
    private var channelData: ChannelData = ChannelData.byDefault(context)
    private val actions: Array<ActionData?> by lazy { arrayOfNulls(Notify.MAXIMUM_ACTIONS) }

    fun asBasic(content: Data.BasicData.() -> Unit): NotifyConfig {
        this.data = Data.BasicData().apply(content)
        return this
    }

    fun asBigText(content: Data.BigTextData.() -> Unit): NotifyConfig {
        this.data = Data.BigTextData().apply(content)
        return this
    }

    fun useChannel(content: ChannelData.()->Unit): NotifyConfig {
        this.channelData = ChannelData().apply(content)
        return this
    }

    fun addAction(action: ActionData.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData().apply(action)
        return this
    }

    fun show() = filterNotify(data)?.show() ?: -1

    private fun filterNotify(data: Data?): Notify<*>? {
        return when (data) {
            is Data.BasicData -> BasicNotify(createNotifyData(data))
            is Data.BigTextData -> BigTextNotify(createNotifyData(data))
            else -> null
        }
    }

    private fun <T : Data> createNotifyData(data: T): NotifyData<T> {
        return NotifyData(
            context = context,
            data = data,
            actions = actions,
            channelData = channelData
        )
    }

}
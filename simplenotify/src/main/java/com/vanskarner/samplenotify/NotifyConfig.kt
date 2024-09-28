package com.vanskarner.samplenotify

import android.content.Context
import com.vanskarner.samplenotify.internal.MAXIMUM_ACTIONS
import com.vanskarner.samplenotify.internal.NotifyGenerator

class NotifyConfig(private val context: Context) {
    private var data: Data? = null
    private var extras: ExtraData = ExtraData()
    private var progressData: ProgressData = ProgressData.byDefault()
    private var channelData: ChannelData = ChannelData.byDefault(context)
    private val actions: Array<ActionData?> by lazy { arrayOfNulls(MAXIMUM_ACTIONS) }

    fun asBasic(content: Data.BasicData.() -> Unit): NotifyConfig {
        this.data = Data.BasicData().apply(content)
        return this
    }

    fun asBigText(content: Data.BigTextData.() -> Unit): NotifyConfig {
        this.data = Data.BigTextData().apply(content)
        return this
    }

    fun asInbox(content: Data.InboxData.() -> Unit): NotifyConfig {
        this.data = Data.InboxData().apply(content)
        return this
    }

    fun asBigPicture(content: Data.BigPictureData.() -> Unit): NotifyConfig {
        this.data = Data.BigPictureData().apply(content)
        return this
    }

    fun asMessaging(content: Data.MessageData.() -> Unit): NotifyConfig {
        this.data = Data.MessageData().apply(content)
        return this
    }

    fun extras(content: ExtraData.() -> Unit): NotifyConfig {
        this.extras = extras.apply(content)
        return this
    }

    fun progress(
        currentPercentage: Int,
        indeterminate: Boolean = false
    ): NotifyConfig {
        this.progressData = progressData.apply {
            this.currentPercentage = currentPercentage
            this.indeterminate = indeterminate
            this.enable = true
        }
        return this
    }

    fun hideProgress(conditionToHide: () -> Boolean = { true }): NotifyConfig {
        this.progressData = progressData.apply {
            this.conditionToHide = conditionToHide
            this.enable = true
        }
        return this
    }

    fun useChannel(content: ChannelData.() -> Unit): NotifyConfig {
        this.channelData = channelData.apply(content)
        return this
    }

    fun addAction(action: ActionData.BasicAction.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData.BasicAction().apply(action)
        return this
    }

    fun addReplyAction(action: ActionData.ReplyAction.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData.ReplyAction().apply(action)
        return this
    }

    fun show(): Int {
        return data?.let {
            NotifyGenerator(
                context = context,
                data = it,
                extra = extras,
                actions = actions,
                channelData = channelData,
                progressData = progressData
            ).show()
        } ?: -1
    }

}
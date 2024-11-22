package com.vanskarner.samplenotify

import android.content.Context
import androidx.core.app.NotificationCompat
import com.vanskarner.samplenotify.internal.INVALID_NOTIFICATION_ID
import com.vanskarner.samplenotify.internal.MAXIMUM_ACTIONS
import com.vanskarner.samplenotify.internal.NotifyGenerator

class NotifyConfig(private val context: Context) {
    private var data: Data? = null
    private var extras: ExtraData = ExtraData()
    private var progressData: ProgressData? = null
    private var stackableData: StackableData? = null
    private var channelId: String? = null
    internal val actions: Array<ActionData?> by lazy { arrayOfNulls(MAXIMUM_ACTIONS) }

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

    fun asDuoMessaging(content: Data.DuoMessageData.() -> Unit): NotifyConfig {
        this.data = Data.DuoMessageData().apply(content)
        return this
    }

    fun asGroupMessaging(content: Data.GroupMessageData.() -> Unit): NotifyConfig {
        this.data = Data.GroupMessageData().apply(content)
        return this
    }

    fun asCall(content: Data.CallData.() -> Unit): NotifyConfig {
        this.data = Data.CallData().apply(content)
        return this
    }

    fun asCustomDesign(content: Data.CustomDesignData.() -> Unit): NotifyConfig {
        this.data = Data.CustomDesignData().apply(content)
        return this
    }

    fun stackable(content: StackableData.() -> Unit): NotifyConfig {
        this.stackableData = StackableData().apply(content)
        return this
    }

    fun extras(content: ExtraData.() -> Unit): NotifyConfig {
        this.extras = extras.apply(content)
        return this
    }

    fun progress(content: ProgressData.() -> Unit): NotifyConfig {
        this.progressData = ProgressData().apply(content)
        return this
    }

    fun useChannel(channelId: String): NotifyConfig {
        this.channelId = channelId
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

    fun generateNotificationPair(): Pair<Int, NotificationCompat.Builder> {
        return NotifyGenerator(
            context = context,
            data = data ?: Data.BasicData(),
            extra = extras,
            actions = actions,
            stackableData = stackableData,
            channelId = channelId,
            progressData = progressData
        ).generateNotificationWithId()
    }

    fun show(): Pair<Int, Int> {
        return data?.let {
            NotifyGenerator(
                context = context,
                data = it,
                extra = extras,
                actions = actions,
                stackableData = stackableData,
                channelId = channelId,
                progressData = progressData
            ).show()
        } ?: Pair(INVALID_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)
    }

}
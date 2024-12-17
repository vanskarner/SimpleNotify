package com.vanskarner.simplenotify

import android.content.Context
import androidx.core.app.NotificationCompat
import com.vanskarner.simplenotify.internal.MAXIMUM_ACTIONS
import com.vanskarner.simplenotify.types.BasicNotify
import com.vanskarner.simplenotify.types.BigPictureNotify
import com.vanskarner.simplenotify.types.BigTextNotify
import com.vanskarner.simplenotify.types.CallNotify
import com.vanskarner.simplenotify.types.ConfigData
import com.vanskarner.simplenotify.types.CustomDesignNotify
import com.vanskarner.simplenotify.types.DuoMessageNotify
import com.vanskarner.simplenotify.types.GroupMessageNotify
import com.vanskarner.simplenotify.types.InboxNotify
import com.vanskarner.simplenotify.types.InvalidNotify

/**
 * NotifyConfig: Configures and builds notifications using a fluent API.
 *
 * This class provides a flexible way to define notification styles, add actions, set progress,
 * and customize notification behavior to suit various scenarios.
 *
 * @param context The context used to access system notification services.
 */
class NotifyConfig(private val context: Context) {
    private var data: Data? = null
    private var extras: ExtraData = ExtraData()
    private var progressData: ProgressData? = null
    private var stackableData: StackableData? = null
    private var channelId: String? = null
    internal val actions: Array<ActionData?> by lazy { arrayOfNulls(MAXIMUM_ACTIONS) }

    /**
     * Configures a basic notification style.
     * It uses by default a notification channel provided by the library.
     *
     * @param content Lambda to define [Data.BasicData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asBasic(content: Data.BasicData.() -> Unit): NotifyConfig {
        this.data = Data.BasicData().apply(content)
        return this
    }

    /**
     * Configures a big text style notification.
     * It uses by default a notification channel provided by the library.
     *
     * @param content Lambda to define [Data.BigTextData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asBigText(content: Data.BigTextData.() -> Unit): NotifyConfig {
        this.data = Data.BigTextData().apply(content)
        return this
    }

    /**
     * Configures an inbox style notification.
     * It uses by default a notification channel provided by the library.
     *
     * @param content Lambda to define [Data.InboxData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asInbox(content: Data.InboxData.() -> Unit): NotifyConfig {
        this.data = Data.InboxData().apply(content)
        return this
    }

    /**
     * Configures a big picture style notification.
     * It uses by default a notification channel provided by the library.
     *
     * @param content Lambda to define [Data.BigPictureData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asBigPicture(content: Data.BigPictureData.() -> Unit): NotifyConfig {
        this.data = Data.BigPictureData().apply(content)
        return this
    }

    /**
     * Set up a messaging style notification between 2 people.
     * Uses by default a notification channel for messaging provided by the library.
     *
     * @param content Lambda to define [Data.DuoMessageData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asDuoMessaging(content: Data.DuoMessageData.() -> Unit): NotifyConfig {
        this.data = Data.DuoMessageData().apply(content)
        return this
    }

    /**
     * Configures a group messaging style notification.
     * Uses by default a notification channel for messaging provided by the library.
     *
     * @param content Lambda to define [Data.GroupMessageData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asGroupMessaging(content: Data.GroupMessageData.() -> Unit): NotifyConfig {
        this.data = Data.GroupMessageData().apply(content)
        return this
    }

    /**
     * Configures a call style notification. Uses by default a notification channel for calls
     * provided by the library.
     *
     * If the type and attributes required by the call type are not provided, then when invoking
     * show or generateBuilder, the notification will not be displayed or the notification will not
     * be generated.
     *
     * @param content Lambda to define [Data.CallData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asCall(content: Data.CallData.() -> Unit): NotifyConfig {
        this.data = Data.CallData().apply(content)
        return this
    }

    /**
     * Configures a custom design notification style.
     * It uses by default a notification channel provided by the library.
     *
     * @param content Lambda to define [Data.CustomDesignData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun asCustomDesign(content: Data.CustomDesignData.() -> Unit): NotifyConfig {
        this.data = Data.CustomDesignData().apply(content)
        return this
    }

    /**
     * Grouping function for active notifications as long as they have the same
     * [ExtraData.groupKey].
     *
     * @param content Lambda to define [StackableData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun stackable(content: StackableData.() -> Unit): NotifyConfig {
        this.stackableData = StackableData().apply(content)
        return this
    }

    /**
     * Additional functions to modify preset behavior
     *
     *  @param content Lambda to define [ExtraData] properties.
     *  @return The current [NotifyConfig] instance.
     */
    fun extras(content: ExtraData.() -> Unit): NotifyConfig {
        this.extras = extras.apply(content)
        return this
    }

    /**
     * Adds a progress bar to the notification. If no ID is specified for the notification type,
     * it will default to -333.
     *
     * When used, a default notification channel is applied for progress provided by the library,
     * except for the type: [asCall], [asDuoMessaging],
     * [asGroupMessaging]
     *
     * @param content Lambda to define [ProgressData] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun progress(content: ProgressData.() -> Unit): NotifyConfig {
        this.progressData = ProgressData().apply(content)
        return this
    }

    /**
     * Set the channel ID for the notification, so as not to use the default channels that have
     * the notification types
     *
     * @param channelId The ID of the notification channel.
     * @return The current [NotifyConfig] instance.
     */
    fun useChannel(channelId: String): NotifyConfig {
        this.channelId = channelId
        return this
    }

    /**
     * Adds a basic action to the notification.
     * A notification can offer up to three action buttons that allow the user to respond quickly
     *
     * @param action Lambda to define [ActionData.BasicAction] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun addAction(action: ActionData.BasicAction.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData.BasicAction().apply(action)
        return this
    }

    /**
     * Adds a reply action to the notification.
     * A notification can offer up to three action buttons that allow the user to respond quickly
     *
     * @param action Lambda to define [ActionData.ReplyAction] properties.
     * @return The current [NotifyConfig] instance.
     */
    fun addReplyAction(action: ActionData.ReplyAction.() -> Unit): NotifyConfig {
        val index = actions.indexOfFirst { it == null }
        if (index != -1) actions[index] = ActionData.ReplyAction().apply(action)
        return this
    }

    /**
     * Generates a [NotificationCompat.Builder] with the current configuration.
     *
     * @return A fully configured [NotificationCompat.Builder] instance, or `null` if the builder
     * cannot be generated.
     */
    fun generateBuilder(): NotificationCompat.Builder? = filter().generateBuilder()

    /**
     * Displays the configured notification.
     *
     * Returns a pair with the notification ID as "first" and the group notification ID as "second".
     * If the [stackable] function was not specified, -1 will be returned for "second".
     * If no notification type is specified, the notification will not be shown and
     * Pair(-1, -1) will be returned.
     *
     * @return A pair with the notification ID and the group notification ID. If no notification
     *         type is specified, Pair(-1, -1) will be returned.
     */
    fun show(): Pair<Int, Int> = filter().show()

    private fun filter(): Notify {
        val requiredData = data ?: return InvalidNotify()
        val configData = ConfigData(
            data = requiredData,
            extras = extras,
            progressData = progressData,
            stackableData = stackableData,
            channelId = channelId,
            actions = actions.toList()
        )
        return when (requiredData) {
            is Data.BasicData -> BasicNotify(context, configData)
            is Data.BigPictureData -> BigPictureNotify(context, configData)
            is Data.BigTextData -> BigTextNotify(context, configData)
            is Data.CallData -> CallNotify(context, configData)
            is Data.CustomDesignData -> CustomDesignNotify(context, configData)
            is Data.DuoMessageData -> DuoMessageNotify(context, configData)
            is Data.GroupMessageData -> GroupMessageNotify(context, configData)
            is Data.InboxData -> InboxNotify(context, configData)
        }
    }

}
package com.vanskarner.simplenotify

import android.content.Context
import com.vanskarner.simplenotify.internal.NotifyChannel

/**
 * SimpleNotify: A fluent API for Android notifications.
 *
 * Simplifies creating notifications, ensuring compatibility across devices and API levels.
 * Enables composing notification styles tailored to different scenarios with minimal effort.
 */
class SimpleNotify {

    companion object {

        private val notifyChannel = NotifyChannel

        /**
         * Creates a new [NotifyConfig] instance to configure and display a notification.
         *
         * @param context The context used to access system notification services.
         * @return A [NotifyConfig] instance for chaining notification configurations.
         */
        fun with(context: Context): NotifyConfig {
            return NotifyConfig(context)
        }

        /**
         * Cancels a specific notification by its ID.
         *
         * @param context The context used to access system notification services.
         * @param notificationId The unique ID of the notification to be canceled.
         */
        fun cancel(context: Context, notificationId: Int) {
            notifyChannel.cancelNotification(context, notificationId)
        }

        /**
         * Cancels all notifications created by the application.
         *
         * @param context The context used to access system notification services.
         */
        fun cancelAll(context: Context) {
            notifyChannel.cancelAllNotification(context)
        }

    }

}
package com.vanskarner.samplenotify.common

import android.app.NotificationManager
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.delay

internal suspend fun NotificationManager.waitActiveNotifications(
    waitItems: Int = 0,
    timeout: Long = 5000L,
    pollingInterval: Long = 100L
): Array<StatusBarNotification> {
    var timeWaited = 0L
    while (timeWaited < timeout) {
        if (activeNotifications.size == waitItems) {
            return activeNotifications
        }
        delay(pollingInterval)
        timeWaited += pollingInterval
    }
    return emptyArray()
}

package com.vanskarner.samplenotify.common

import android.app.NotificationManager
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.delay

suspend fun waitForActiveNotifications(
    manager: NotificationManager,
    timeout: Long = 5000L,
    pollingInterval: Long = 100L
): Array<StatusBarNotification> {
    var timeWaited = 0L
    while (timeWaited < timeout) {
        if (manager.activeNotifications.isNotEmpty()) return manager.activeNotifications
        delay(pollingInterval)
        timeWaited += pollingInterval
    }
    return emptyArray()
}

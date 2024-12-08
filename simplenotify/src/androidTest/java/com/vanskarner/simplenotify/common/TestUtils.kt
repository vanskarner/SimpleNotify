package com.vanskarner.simplenotify.common

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import org.junit.Assert.assertNotNull
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.simplenotify.Data
import com.vanskarner.simplenotify.NotifyMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.coroutines.CoroutineContext

internal suspend fun NotificationManager.waitNotificationDisappear(
    notificationId: Int,
    context: CoroutineContext = Dispatchers.IO,
    timeout: Long = 5000L,
    pollingInterval: Long = 100L
): Boolean {
    var timeWaited = 0L
    while (timeWaited < timeout) {
        if (!activeNotifications.any { it.id == notificationId }) return true
        withContext(context) { Thread.sleep(pollingInterval) }
        timeWaited += pollingInterval
    }
    return false
}

internal suspend fun NotificationManager.waitForNotification(
    notificationId: Int,
    context: CoroutineContext = Dispatchers.IO,
    timeout: Long = 5000L,
    pollingInterval: Long = 100L
): StatusBarNotification {
    var timeWaited = 0L
    while (timeWaited < timeout) {
        val result = activeNotifications.find { it.id == notificationId }
        if (result != null) return result
        withContext(context) { Thread.sleep(pollingInterval) }
        timeWaited += pollingInterval
    }
    val errorMsg = "Notification with ID $notificationId not found within $timeout ms"
    throw IllegalStateException(errorMsg)
}

internal suspend fun NotificationManager.waitForAllNotificationsPresents(
    notificationIds: Set<Int>,
    context: CoroutineContext = Dispatchers.IO,
    timeout: Long = 5000L,
    pollingInterval: Long = 100L
): Boolean {
    var timeWaited = 0L
    while (timeWaited < timeout) {
        val matchedIds = activeNotifications.map { it.id }.toSet()
        if (notificationIds.all { it in matchedIds }) return true
        withContext(context) { Thread.sleep(pollingInterval) }
        timeWaited += pollingInterval
    }
    return false
}

internal fun Icon.toBitmap(): Bitmap? {
    return when (type) {
        Icon.TYPE_BITMAP ->
            (loadDrawable(ApplicationProvider.getApplicationContext()) as? BitmapDrawable)?.bitmap

        else -> null
    }
}

internal fun <T> Bundle.getCustomParcelable(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}

internal fun assertChannelIDValidity(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdResult = manager.getNotificationChannel(channelId)?.id

        assertNotNull("The channel does not exist", channelIdResult)
    }
}

internal fun assertNotificationChannelId(expectedId: String, actualNotification: Notification) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //Channels available from API 26
        assertEquals(expectedId, actualNotification.channelId)
    }
}

internal fun assertCommonData(expectedData: Data, actualNotification: Notification) {
    val extras = actualNotification.extras
    val actualLargeIcon =
        extras.getCustomParcelable(NotificationCompat.EXTRA_LARGE_ICON, Icon::class.java)
    val actualAutoCancel = actualNotification.flags and NotificationCompat.FLAG_AUTO_CANCEL != 0

    assertEquals(expectedData.smallIcon, actualNotification.smallIcon.resId)
    assertTrue(expectedData.largeIcon?.sameAs(actualLargeIcon?.toBitmap()) ?: false)
    assertEquals(expectedData.contentIntent, actualNotification.contentIntent)
    assertEquals(expectedData.autoCancel, actualAutoCancel)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        assertEquals(expectedData.timeoutAfter, actualNotification.timeoutAfter)
    }
}

internal fun assertNotificationSound(expectedUri: Uri, actualNotification: Notification) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        @Suppress("DEPRECATION")
        assertEquals(expectedUri, actualNotification.sound)
    }
}

internal fun assertNotificationPriority(expectedPriority: Int, actualNotification: Notification) {
    @Suppress("DEPRECATION")//no other way to get it for API versions >=25
    assertEquals(expectedPriority, actualNotification.priority)
}

internal fun assertNotificationMessages(
    messages: ArrayList<NotifyMessaging>,
    actualNotification: Notification
) {
    val actualExtras = actualNotification.extras

    @Suppress("DEPRECATION")//more accessible way to check values for API >=32
    val actualMessages = actualExtras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)
    assertEquals(messages.size, actualMessages?.size)
}

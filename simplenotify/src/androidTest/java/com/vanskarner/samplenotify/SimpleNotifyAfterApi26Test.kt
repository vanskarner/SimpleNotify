package com.vanskarner.samplenotify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.simplenotify.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class SimpleNotifyAfterApi26Test {
    private lateinit var appContext: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Espresso.onIdle()
        notificationManager.cancelAll()
    }

    @Test
    fun with_usingBasicFormAndNoId_shouldBeShow() {
        val expectedNotificationId = SimpleNotify.with(appContext)
            .asBasic {
                title = "Any title"
                text = "Any text"
            }
            .show()
        val statusBarNotification =
            notificationManager.activeNotifications.first()
        val actualNotification = statusBarNotification.notification

        assertNotNull(actualNotification)
        assertEquals(DEFAULT_CHANNEL_ID, actualNotification.channelId)
        assertEquals(expectedNotificationId, statusBarNotification.id)
        assertEquals(
            "Any title",
            actualNotification.extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals("Any text", actualNotification.extras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Test
    fun with_usingBasicFormAndWithId_shouldBeShow() {
        SimpleNotify.with(appContext)
            .asBasic {
                id = 123
                title = "Any title"
                text = "Any text"
            }
            .show()
        val statusBarNotification =
            notificationManager.activeNotifications.first()
        val actualNotification = statusBarNotification.notification

        assertNotNull(actualNotification)
        assertEquals(DEFAULT_CHANNEL_ID, actualNotification.channelId)
        assertEquals(123, statusBarNotification.id)
        assertEquals(
            "Any title",
            actualNotification.extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals("Any text", actualNotification.extras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Test
    fun cancel_shouldBeCancel() {
        val testChannel =
            NotificationChannel("testId1", "Test Name1", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(testChannel)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, testChannel.id)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == notificationId }
        assertNotNull(statusBarNotification.notification)
        SimpleNotify.cancel(appContext, notificationId)
        Espresso.onIdle()
        assertEquals(0, notificationManager.activeNotifications.size)
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel() {
        val testChannel =
            NotificationChannel("testId2", "Test Name2", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(testChannel)
        val notificationId = 123
        val notifyBuilder = NotificationCompat.Builder(appContext, testChannel.id)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Any Title")
            .setContentText("Any Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, notifyBuilder.build())

        Espresso.onIdle()
        val statusBarNotification =
            notificationManager.activeNotifications.first { it.id == notificationId }
        assertNotNull(statusBarNotification.notification)
        SimpleNotify.cancelAll(appContext)
        Espresso.onIdle()
        assertEquals(0, notificationManager.activeNotifications.size)
    }

}
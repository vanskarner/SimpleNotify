package com.vanskarner.samplenotify

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.common.ConditionalPermissionRule
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitActiveNotifications
import com.vanskarner.samplenotify.internal.DEFAULT_CHANNEL_ID
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class SimpleNotifyAfterApi26Test {
    private lateinit var context: Context
    private lateinit var manager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @After
    fun tearDown() {
        manager.cancelAll()
    }

    @Test
    fun with_usingBasicFormAndNoId_shouldBeShow() = runTest {
        val expectedNotificationId = SimpleNotify.with(context)
            .asBasic {
                title = "Any title"
                text = "Any text"
            }
            .show()
        val activeNotifications = manager.waitActiveNotifications(1)
        val actualStatusBarNotification = activeNotifications.first()
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras

        assertEquals(DEFAULT_CHANNEL_ID, actualNotification.channelId)
        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        assertEquals("Any title", actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals("Any text", actualExtras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Test
    fun with_usingBasicFormAndWithId_shouldBeShow() = runTest {
        SimpleNotify.with(context)
            .asBasic {
                id = 123
                title = "Any title"
                text = "Any text"
            }
            .show()
        val activeNotifications = manager.waitActiveNotifications(1)
        val actualStatusBarNotification = activeNotifications.first()
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras

        assertEquals(DEFAULT_CHANNEL_ID, actualNotification.channelId)
        assertEquals(123, actualStatusBarNotification.id)
        assertEquals("Any title", actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals("Any text", actualExtras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Test
    fun cancel_shouldBeCancel() = runTest {
        val testChannel = TestDataProvider.notificationChannel()
        manager.createNotificationChannel(testChannel)
        val expectedNotificationId = 124
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannel.id)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        manager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = manager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        SimpleNotify.cancel(context, expectedNotificationId)
        assertEquals(0, manager.waitActiveNotifications(0).size)
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel() = runTest {
        val testChannel = TestDataProvider.notificationChannel()
        manager.createNotificationChannel(testChannel)
        val expectedNotificationId = 125
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannel.id)
        manager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = manager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        SimpleNotify.cancelAll(context)
        assertEquals(0, manager.waitActiveNotifications(0).size)
    }

}
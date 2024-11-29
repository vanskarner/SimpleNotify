package com.vanskarner.simplenotify

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertNotificationChannelId
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.common.waitNotificationDisappear
import com.vanskarner.simplenotify.internal.DEFAULT_CHANNEL_ID
import com.vanskarner.simplenotify.SimpleNotify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleNotifyTest {
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
    fun with_usingBasicForm_shouldBeShow() = runTest {
        val expectedNotificationId = SimpleNotify.with(context)
            .asBasic {
                title = "Any title"
                text = "Any text"
            }
            .show().first
        val actualStatusBarNotification = manager.waitForNotification(expectedNotificationId)
        val actualNotification = actualStatusBarNotification.notification
        val actualExtras = actualNotification.extras

        assertNotificationChannelId(DEFAULT_CHANNEL_ID, actualNotification)
        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        assertEquals("Any title", actualExtras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals("Any text", actualExtras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Test
    fun cancel_shouldBeCancel() = runTest {
        val testChannelId = TestDataProvider.createChannel(manager)
        val expectedNotificationId = 2
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannelId)
        manager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = manager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        SimpleNotify.cancel(context, expectedNotificationId)
        assertTrue(manager.waitNotificationDisappear(expectedNotificationId))
    }

    @Test
    fun cancelAllNotifications_shouldBeCancel() = runTest {
        val testChannelId = TestDataProvider.createChannel(manager)
        val expectedNotificationId = 3
        val notifyBuilder = TestDataProvider.basicNotification(context, testChannelId)
        manager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification = manager.waitForNotification(expectedNotificationId)

        assertEquals(expectedNotificationId, actualStatusBarNotification.id)
        SimpleNotify.cancelAll(context)
        assertTrue(manager.waitNotificationDisappear(expectedNotificationId))
    }

}
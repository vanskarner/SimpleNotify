package com.vanskarner.simplenotify.internal

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.simplenotify.common.ConditionalPermissionRule
import com.vanskarner.simplenotify.common.TestDataProvider
import com.vanskarner.simplenotify.common.assertChannelIDValidity
import com.vanskarner.simplenotify.common.waitForNotification
import com.vanskarner.simplenotify.common.waitNotificationDisappear
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotifyChannelTest {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @get:Rule
    val permissionRule = ConditionalPermissionRule(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifyChannel = NotifyChannel
    }

    @After
    fun tearDown() {
        notificationManager.cancelAll()
    }

    @Test
    fun applyDefaultChannel_accordingToVersionShouldBeCreated() {
        val actualChannelId = notifyChannel.applyDefaultChannel(context)

        assertChannelIDValidity(actualChannelId)
        assertEquals(DEFAULT_CHANNEL_ID, actualChannelId)
    }

    @Test
    fun applyProgressChannel_accordingToVersionShouldBeCreated() {
        val actualChannelId = notifyChannel.applyProgressChannel(context)

        assertChannelIDValidity(actualChannelId)
        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, actualChannelId)
    }

    @Test
    fun applyCallChannel_accordingToVersionShouldBeCreated() {
        val actualChannelId = notifyChannel.applyCallChannel(context)

        assertChannelIDValidity(actualChannelId)
        assertEquals(DEFAULT_CALL_CHANNEL_ID, actualChannelId)
    }

    @Test
    fun checkChannelNotExists_whenNoExist_shouldBeTrue() {
        val channelResult = notifyChannel.checkChannelNotExists(context, "NotExist")

        assertTrue(channelResult)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() = runTest {
        val expectedNotificationId = 1
        val statusBarNotification = waitForNotificationShow(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification.id)
        notifyChannel.cancelNotification(context, expectedNotificationId)
        assertTrue(notificationManager.waitNotificationDisappear(expectedNotificationId))
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() = runTest {
        val expectedNotificationId = 2
        val statusBarNotification = waitForNotificationShow(expectedNotificationId)

        assertEquals(expectedNotificationId, statusBarNotification.id)
        notifyChannel.cancelAllNotification(context)
        assertTrue(notificationManager.waitNotificationDisappear(expectedNotificationId))
    }

    private suspend fun waitForNotificationShow(notificationId: Int): StatusBarNotification {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val notifyBuilder = TestDataProvider.basicNotification(context, channelId)
        notificationManager.notify(notificationId, notifyBuilder.build())
        return notificationManager.waitForNotification(notificationId)
    }

}
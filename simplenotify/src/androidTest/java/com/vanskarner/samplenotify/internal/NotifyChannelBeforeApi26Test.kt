package com.vanskarner.samplenotify.internal

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.vanskarner.samplenotify.common.TestDataProvider
import com.vanskarner.samplenotify.common.waitActiveNotifications
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.N_MR1)
class NotifyChannelBeforeApi26Test {
    private lateinit var notifyChannel: NotifyChannel
    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

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
    fun applyDefaultChannel_shouldOnlyReturnId() {
        val channelId = notifyChannel.applyDefaultChannel(context)

        assertEquals(DEFAULT_CHANNEL_ID, channelId)
    }

    @Test
    fun applyProgressChannel_shouldOnlyReturnId() {
        val channelId = notifyChannel.applyProgressChannel(context)

        assertEquals(DEFAULT_PROGRESS_CHANNEL_ID, channelId)
    }

    @Test
    fun checkChannelNotExists_shouldOnlyReturnTrue() {
        val channel1 = notifyChannel.checkChannelNotExists(context, "anyId")

        assertTrue(channel1)
    }

    @Test
    fun cancelNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val expectedNotificationId = 123
        val notifyBuilder = TestDataProvider.basicNotification(context,channelId)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        notifyChannel.cancelNotification(context, expectedNotificationId)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }

    @Test
    fun cancelAllNotification_shouldBeCanceled() = runTest {
        val channelId = notifyChannel.applyDefaultChannel(context)
        val expectedNotificationId = 123
        val notifyBuilder = TestDataProvider.basicNotification(context,channelId)
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        notificationManager.notify(expectedNotificationId, notifyBuilder.build())
        val actualStatusBarNotification =
            notificationManager.waitActiveNotifications(1).firstOrNull()

        assertEquals(expectedNotificationId, actualStatusBarNotification?.id)
        notifyChannel.cancelAllNotification(context)
        assertEquals(0, notificationManager.waitActiveNotifications(0).size)
    }

}